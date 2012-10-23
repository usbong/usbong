/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;
import org.openmrs.util.OpenmrsUtil;

/*
 * Main:
 * inserts a patient record into the openmrs database
 *  
 * This Eclipse Project requires the openmrs-api Eclipse project to work
 */

public class Main
{	
	
	// global variables
	private static Date eDate = null;	    // encounter date
	private static int locationId = 1;     // location where encounter happened
//	private int formNum = 5;       // form used
	private static User creator = null;  // creator used

	// other constants
	private  static final Integer userId_admin = 1;
	
	// constants for array indexes
//	private  final int pData_EncounterDate = 0;
//	private  final int pData_LocationID = 1;
	//Sample data (i.e. description) from usbong server DB
	//Any;Oct22,2012;Any;S;M;B;0;Sept19,2012;Marikina City;Any
	//the first two Any's correspond to textDisplay screens
	private  static final int pData_EncounterDate = 1;
	private  static final int pData_FamilyName = 3;
	private  static final int pData_GivenName = 4;
	private  static final int pData_MiddleName = 5;
	private  static final int pData_Gender = 6;
	private  static final int pData_BirthDate = 7;
	private  static final int pData_Address = 8;
	//	private  final int pData_MotherName = 8;

	
//	private  final Integer personAttributeTypeId_MotherName = 4;	
	private  static final Integer patientIdentifierTypeId_OldIdentificationNumber = 2;//2;
	
	private static String ipAddress = "localhost";
	private static String usbongServerUrl = "jdbc:mysql://"+ipAddress+":3306/usbong?autoReconnect=true";

	// for retrieving data from the usbong server database
	private static int caseNo = 1;

	//mysql username and password; change as needed
	private static String userid ="root";
	private static String password = "mysqlQwer1234"; 

	
	// runs the insertIntoOpenMRS program
	public static void main(String[] args) throws DatabaseUpdateException, InputRequiredException
	{
		System.out.println("OpenmrsUtil.getApplicationDataDirectory(): "+OpenmrsUtil.getApplicationDataDirectory());
		  File propsFile = new File(OpenmrsUtil.getApplicationDataDirectory(), "openmrs-runtime.properties");
		  Properties props = new Properties();
		  OpenmrsUtil.loadProperties(props, propsFile);
//		  Context.startup("jdbc:mysql://localhost:3306/db-name?autoReconnect=true", "openmrs-db-user", "3jknfjkn33ijt", props);

		  try {
//			  Context.updateDatabase(null);
			  Context.startup(props.getProperty("connection.url"), props.getProperty("connection.username"), props.getProperty("connection.password"), props);				 		  
	//		  Context.startup("jdbc:mysql://localhost:3306/openmrs?autoReconnect=true", "root", "mysqlQwer1234", new Properties());//props);		
		  }
		  catch (Exception e){
		    Context.openSession();
		    Context.authenticate("admin", "openmrsQwer1234");

		    /*
		    List<Patient> patients = Context.getPatientService().getPatients("John");
		    for (Patient patient : patients) {
		      System.out.println("Found patient with name " + patient.getPersonName() + " and uuid: " + patient.getUuid());
		    }
		    */
		    
			Boolean run = true;
			while(run) // need to manually stop program loop
			{
				try
				{										
					initInsertPatient(); // READS FROM usbong server DATABASE					
//					run = false;
					Thread.sleep(5000); // read from usbong server database every 5 seconds???
				}
				catch(Exception inner_e)
				{
					inner_e.printStackTrace();
					run = false;
				}								
		    
//		    insertPatient();
		    
		    System.out.println("Process Complete.");
			}
		  }
		  finally {
		    Context.closeSession();
		  }
	}

	
	/*
	 * Reference: http://code.google.com/p/e-imci/downloads/detail?name=PARSERandDB.zip&can=2&q=;
	 * last accessed: 19 Oct. 2012
	 */
	public static Connection getUsbongServerConnection()
	{	
		try 
		{
			//Class.forName("myDriver.ClassName"); 
			Class.forName("com.mysql.jdbc.Driver");	
		} 
		catch(java.lang.ClassNotFoundException e)
		{
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		Connection con = null;
		try 
		{
			con = DriverManager.getConnection(usbongServerUrl,userid, password);
		}
		catch(SQLException e) 
		{			
			System.err.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}
		return con;
	}

	/*
	 * Reference: http://code.google.com/p/e-imci/downloads/detail?name=PARSERandDB.zip&can=2&q=;
	 * last accessed: 19 Oct. 2012
	 */
	public static void initInsertPatient() {
		Connection con = getUsbongServerConnection();
		String selectString;
		selectString = "select * from output where has_been_sent_to_main_db='0'";	    

		try 
		{
			PreparedStatement pstmt = con.prepareStatement("");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(selectString);
			
			while (rs.next()) 
			{		    
			    int id = rs.getInt("output_id");	
//			    boolean has_been_sent_to_main_db = rs.getBoolean("has_been_sent_to_main_db");
//			    String originator = rs.getString("description");
			    String description = rs.getString("description");						    			    
			    			    
//			    addData(text, originator); // ADD SMS DATA INTO openmrs DATABASE
			    addData(description); // ADD PATIENT DATA FROM usbong server DB INTO openmrs DATABASE
						    
			    try // set has_been_sent_to_main_db to true (i.e. 1) after processing
				{
					pstmt = con.prepareStatement("UPDATE output SET has_been_sent_to_main_db='1' WHERE output_id=?");
					pstmt.setInt(1, id);
			   		pstmt.executeUpdate();
				} 
			    catch(SQLException e) 
				{
			    	System.err.println("SQLException: " + e.getMessage());
					e.printStackTrace();
				}			    			   
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!			    			
			}						
			pstmt.close();
			stmt.close();
			con.close();
		} 
		catch(SQLException e) 
		{
			System.err.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}		
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    COMMENT OUT   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//		JOptionPane.showMessageDialog(null, "See Console for More Info");
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		System.out.println(">>>>> ADDING PATIENT DATA TO OPENMRS DB COMPLETE.");
	}

	/*
	 * Reference: http://code.google.com/p/e-imci/downloads/detail?name=PARSERandDB.zip&can=2&q=;
	 * last accessed: 19 Oct. 2012
	 */
	public static void addData(String line)//, String originator)
	{	      	
      	try 
      	{
				ArrayList<String> pData = new ArrayList<String>(); // patient data
/*				
				ArrayList<String> iData = new ArrayList<String>(); // answers to initial IMCI questions
				ArrayList<String> input = new ArrayList<String>(); // answers to IMCI questions
				String overallComments = "";
*/				
						        		      
		        Scanner group = new Scanner(line);
		        group.useDelimiter("~");
		       
		        int grpNum = 0; // grpnum = group that the answers belong to
		        Patient patient = null;
		        Encounter e = null;
		        
		        
		        while (group.hasNext()) 
				{		        			        	
		        	Scanner contents = new Scanner(group.next());		        	
		        	contents.useDelimiter(";");
		        			        	
		        	
		        	// COLLECT DATA !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		        	while (contents.hasNext())
		        	{
		        		String s = contents.next();		        		
			        	
			        	if(grpNum ==  0) // patient data
			        	{
			        		pData.add(s);
			        	}
/*
			        	else if(grpNum == 1) // answers to initial IMCI questions 
			        	{
			        		iData.add(s);
			        	}
			        	else if(!group.hasNext())
			        	{
			        		overallComments = s;
			        	}
			        	else
			        	{
			        		input.add(s); // answers processed by IMCI decision tree
			        	}
			        	//System.out.println(grpNum + ": " + s);
*/
		        	}	        	
/*		        	
		        	grpNum++; // go to next group of answers
*/		        	
				}		      
		        		        
/*		        
		        // PARSE INPUT using Mike's parser
	        	kXMLTrial trial = new kXMLTrial();
	    		trial.actionPerformed(input);
*/	    			    			    		
	    		// CREATE PATIENT
	    		patient = insertPatient(pData);	    		
	    		
	    		// println saved patient details
	    		System.out.println("\n\n\n");	    			    		 
	    		System.out.println("Case " + caseNo +": " + patient);
		        System.out.print(" ");
				for(String p : pData)
				{
					System.out.print(p + ";");
				}
				System.out.println();
/*
				
				
				// set type of form to use
				if(iData.get(iData_TypeOfVisit).equals("Y"))
        		{
        			formNum = INIT_FORM_NUM;        			
        		}
        		else
        		{
        			formNum = RET_FORM_NUM;
        		}
				
				
				
				
				// CREATE ENCOUNTER and save obs "Age"
        		e = createEncounter(patient, pData.get(pData_BirthDate), originator);
        		
        		// insert initial data
				insertGroup(grpNum, e, patient, iData);
	    						
				
				
				
				// println saved initial data details
				System.out.print(" ");
				for(String a : iData)
				{
					System.out.print(a + ";");
				}
				System.out.println();
	    					
				// insert eIMCI Questions and Answers				
				int cID = 0; // concept ID
				double ans = 0.0; // value for a boolean Y or N ans
				Obs o = null; // observation
				ConceptDatatype cDT;
								
				// FOR LOOP START				
				for(int iii=0; iii<trial.Ques.size(); iii++)
				{
					//Add the Observation					
			        cID = Context.getConceptService().getConcept(trial.Ques.get(iii)).getConceptId();
			        cDT = Context.getConceptService().getConcept(trial.Ques.get(iii)).getDatatype();			        
			        if(cDT.isNumeric())
			        {			        			        	
			        	o = insertObsNum(patient, cID, Double.parseDouble(trial.Ans.get(iii)));
			        }
			        else if(cDT.isBoolean())
			        {				        	
				        ans = getObsValue(trial.Ans.get(iii));
				        o = insertObsNum(patient, cID, ans); 
			        }
			        else if(cDT.isText())
			        {			        	
			        	o = insertObsText(patient, cID, trial.Ans.get(iii)); 
			        }			 				  
			        // add obs
			        e.addObs(o); 
				}
				// FOR LOOP UNTIL HERE			   
				
			    // insert Overall Comments 
				cID = Context.getConceptService().getConcept("Overall Comments").getConceptId();
				o = insertObsText(patient, cID, overallComments);
				e.addObs(o);
				
				// save encounter
			    Context.getEncounterService().saveEncounter(e); 
			    
			   	// println saved eIMCI answers		    
				System.out.print(" ");				
				for(String a : input)
				{
					System.out.print(a + ";");
				}
				System.out.println();
*/				
				// go to next patient
				caseNo++; 
				//JOptionPane.showMessageDialog(null,"Encounter for<"+ patient +"> saved.");
		} 
      	catch (Exception e) 
      	{		
			e.printStackTrace();
		}
 
	}

	/*
	 * Reference: http://code.google.com/p/e-imci/downloads/detail?name=PARSERandDB.zip&can=2&q=;
	 * last accessed: 19 Oct. 2012
	 */
	public static Patient insertPatient(ArrayList<String> pData)
	{		
/*		eDate = createDate(pData.get(pData_EncounterDate));
 */
      	String patientFamilyName = pData.get(pData_FamilyName), 
      		   patientGivenName = pData.get(pData_GivenName), 
      		   patientMiddleName = pData.get(pData_MiddleName); 
 /*
      	locationId = Integer.parseInt(pData.get(pData_LocationID));
*/

		eDate = createDate(pData.get(pData_EncounterDate));//"Oct19,2012");//"10 19 2012";
      	locationId = 1;//Integer.parseInt(pData.get(pData_LocationID));

		// Create Person Name
		PersonName name = new PersonName();
		name.setFamilyName(patientFamilyName);
		name.setGivenName(patientGivenName);
		name.setMiddleName(patientMiddleName);
      	
		// Create new Person
		Person person = new Person();
		creator = Context.getUserService().getUser(userId_admin);

//		person.setGender("Male");//pData.get(pData_Gender));
		
		System.out.println(">>>>> pData.get(pData_Gender): "+pData.get(pData_Gender));
		if (pData.get(pData_Gender).equals("0")) {
			person.setGender("Male");
		}
		else{
			person.setGender("Female");			
		}

		person.setCreator(creator);
		Date bDate = new Date();
		bDate = createDate(pData.get(pData_BirthDate)); //"07 29 2012";
		person.setBirthdate(bDate);
		person.setDateCreated(eDate);
		person.addName(name);
		
		// Create Person Address
		PersonAddress address = new PersonAddress();
		address.setAddress1(pData.get(pData_Address));
		person.addAddress(address);
/*		
		// Set Mother's Name
		PersonAttributeType type = Context.getPersonService().getPersonAttributeType(personAttributeTypeId_MotherName);
		PersonAttribute mother = new PersonAttribute(type, pData.get(pData_MotherName));
		person.addAttribute(mother);		
*/				
		// Retrieve Identifier Type and Location
		PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(patientIdentifierTypeId_OldIdentificationNumber);	
		Location location = Context.getLocationService().getLocation(locationId);
		
		// Create new Identifier
		int index = Context.getPatientService().getAllPatients().size() - 1;
		int pID = Context.getPatientService().getAllPatients().get(index).getId()+1;
		
		String identifier = "Patient#" + pID;
		PatientIdentifier patientIdentifier = new PatientIdentifier(identifier, identifierType, location);
		
		// Create new Patient
		Patient patient = new Patient(person);
		patient.addIdentifier(patientIdentifier);
		
		Patient created = Context.getPatientService().savePatient(patient);					
		//JOptionPane.showMessageDialog(null,"Inserted <"+ created +"> into patient Table");			
		return patient;
	}
	
	//THIS IS FOR DUMMY DATA TESTING ONLY
	/*
	 * Reference: http://code.google.com/p/e-imci/downloads/detail?name=PARSERandDB.zip&can=2&q=;
	 * last accessed: 29 July 2012
	 */
	public static Patient insertPatient()//ArrayList<String> pData)
	{
/*		
		eDate = createDate(pData.get(pData_EncounterDate));
      	String pFN = pData.get(pData_FamilyName), pGN = pData.get(pData_GivenName), pMN = pData.get(pData_MiddleName); 
      	locationId = Integer.parseInt(pData.get(pData_LocationID));
*/

		eDate = createDate("09 7 2012");
      	locationId = 1;//Integer.parseInt(pData.get(pData_LocationID));

		
      	String patientFamilyName = "Syson",
      		   patientGivenName = "Michael", 
      		   patientMiddleName = "Bautista"; 

		// Create Person Name
		PersonName name = new PersonName();
		name.setFamilyName(patientFamilyName);
		name.setGivenName(patientGivenName);
		name.setMiddleName(patientMiddleName);
      	
		// Create new Person
		Person person = new Person();
		creator = Context.getUserService().getUser(userId_admin);
		person.setGender("Male");//pData.get(pData_Gender));
		person.setCreator(creator);
		Date bDate = new Date();
		bDate = createDate("07 29 2012");//pData.get(pData_BirthDate)); //"07 29 2012";
		person.setBirthdate(bDate);
		person.setDateCreated(eDate);
		person.addName(name);
		
		// Create Person Address
		PersonAddress address = new PersonAddress();
		address.setAddress1("Marikina City");//pData.get(pData_Address));
		person.addAddress(address);
/*		
		// Set Mother's Name
		PersonAttributeType type = Context.getPersonService().getPersonAttributeType(personAttributeTypeId_MotherName);
		PersonAttribute mother = new PersonAttribute(type, pData.get(pData_MotherName));
		person.addAttribute(mother);		
*/				
		// Retrieve Identifier Type and Location
		PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(patientIdentifierTypeId_OldIdentificationNumber);	
		Location location = Context.getLocationService().getLocation(locationId);
		
		// Create new Identifier
		int index = Context.getPatientService().getAllPatients().size() - 1;
		int pID = Context.getPatientService().getAllPatients().get(index).getId()+1;
		
		String identifier = "Patient#" + pID;
		PatientIdentifier patientIdentifier = new PatientIdentifier(identifier, identifierType, location);
		
		// Create new Patient
		Patient patient = new Patient(person);
		patient.addIdentifier(patientIdentifier);
		
		Patient created = Context.getPatientService().savePatient(patient);					
		//JOptionPane.showMessageDialog(null,"Inserted <"+ created +"> into patient Table");			
		return patient;
	}
	
	// reference: http://www.kodejava.org/examples/19.html;
	//last accessed: 7 Sept. 2012
	public static Date createDate(String fDate) 
	{
		System.out.println("fDate: "+fDate);
		
		//Sample fDate: Oct19,2012
        Scanner rawDate = new Scanner(fDate);
        rawDate.useDelimiter(",");
       
        String monthAndDay = rawDate.next(); //get the month and day
        String month="";
        String day="";
        String year=fDate.replace(monthAndDay+",","");
        
        if (monthAndDay.contains("Jan")) {
        	month="01";
        	day = monthAndDay.replace("Jan", "");
        }
        else if (monthAndDay.contains("Feb")) {
        	month="02";
        	day = monthAndDay.replace("Feb", "");
        }
        else if (monthAndDay.contains("March")) {
        	month="03";
        	day = monthAndDay.replace("March", "");
        }
        else if (monthAndDay.contains("April")) {
        	month="04";
        	day = monthAndDay.replace("April", "");
        }
        else if (monthAndDay.contains("May")) {
        	month="05";
        	day = monthAndDay.replace("May", "");
        }
        else if (monthAndDay.contains("June")) {
        	month="06";
        	day = monthAndDay.replace("June", "");
        }
        else if (monthAndDay.contains("July")) {
        	month="07";
        	day = monthAndDay.replace("July", "");
        }
        else if (monthAndDay.contains("Aug")) {
        	month="08";
        	day = monthAndDay.replace("Aug", "");
        }
        else if (monthAndDay.contains("Sept")) {
        	month="09";
        	day = monthAndDay.replace("Sept", "");
        }
        else if (monthAndDay.contains("Oct")) {
        	month="10";
        	day = monthAndDay.replace("Oct", "");
        }
        else if (monthAndDay.contains("Nov")) {
        	month="11";
        	day = monthAndDay.replace("Nov", "");
        }
        else if (monthAndDay.contains("Dec")) {
        	month="12";
        	day = monthAndDay.replace("Dec", "");
        }

        String finalDate = month+" "+day+" "+year;
		System.out.println("finalDate: "+finalDate);
        
		Date cDate = null;
		DateFormat df = new SimpleDateFormat("MM dd yyyy");		 
        
		try
        {
            cDate = df.parse(finalDate);                                    
        } 
		catch (ParseException e)
        {
            e.printStackTrace();
        }
		
		return cDate;
	}
}