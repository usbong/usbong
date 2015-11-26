package usbong.android;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import usbong.android.utils.UsbongConstants;
import android.os.Parcel;
import android.os.Parcelable;

public class UdteaObject implements Parcelable {
	private static String myTree;
	private static String currUsbongNode;
	private static String nextUsbongNodeIfYes;
	private static String nextUsbongNodeIfNo;
	private static int usbongAnswerContainerCounter;
	private static int usbongNodeContainerCounter;
	private static Vector<String> decisionTrackerContainer;
	private static Vector<String> usbongAnswerContainer;
	private static Vector<String> usbongNodeContainer;
	private static int decisionTrackerContainerSize;
	private static int usbongAnswerContainerSize;
	private static int usbongNodeContainerSize;
	
	public UdteaObject() { //added by Mike, 20151126
		// TODO Auto-generated constructor stub
	}

    UdteaObject(JSONObject jO) throws JSONException {
    	myTree = jO.optString(UsbongConstants.MY_TREE);
    	currUsbongNode = jO.optString(UsbongConstants.CURR_USBONG_NODE);
    	nextUsbongNodeIfYes = jO.optString(UsbongConstants.NEXT_USBONG_NODE_IF_YES);
    	nextUsbongNodeIfNo = jO.optString(UsbongConstants.NEXT_USBONG_NODE_IF_NO);

    	usbongAnswerContainerCounter = jO.optInt(UsbongConstants.USBONG_ANSWER_CONTAINER_COUNTER);
    	usbongNodeContainerCounter = jO.optInt(UsbongConstants.USBONG_NODE_CONTAINER_COUNTER);
    	decisionTrackerContainerSize = jO.optInt(UsbongConstants.DECISION_TRACKER_CONTAINER_SIZE);
    	decisionTrackerContainer = new Vector<String>(decisionTrackerContainerSize);
    	for (int i=0; i<decisionTrackerContainerSize; i++) {
    		decisionTrackerContainer.add(jO.optString(UsbongConstants.DECISION_TRACKER_CONTAINER + i));
    	}
    	usbongAnswerContainerSize = jO.optInt(UsbongConstants.USBONG_ANSWER_CONTAINER_SIZE);
    	usbongAnswerContainer = new Vector<String>(usbongAnswerContainerSize);
    	for (int i=0; i<usbongAnswerContainerSize; i++) {
    		usbongAnswerContainer.add(jO.optString(UsbongConstants.USBONG_ANSWER_CONTAINER + i));
    	}
    	usbongNodeContainerSize = jO.optInt(UsbongConstants.USBONG_NODE_CONTAINER_SIZE);
    	usbongNodeContainer = new Vector<String>(usbongNodeContainerSize);
    	for (int i=0; i<usbongNodeContainerSize; i++) {
    		usbongNodeContainer.add(jO.optString(UsbongConstants.USBONG_NODE_CONTAINER + i));
    	}
    }
    
    public void setMyTree(String s) {
		myTree = s;
	}

    public void setCurrUsbongNode(String s) {
		currUsbongNode = s;
	}

	public void setNextUsbongNodeIfYes(String s) {
		nextUsbongNodeIfYes = s;		
	}

	public void setNextUsbongNodeIfNo(String s) {
		nextUsbongNodeIfNo = s;		
	}

	public void setUsbongAnswerContainerCounter(int c) {
		usbongAnswerContainerCounter = c;
	}

	public void setUsbongNodeContainerCounter(int c) {
		usbongNodeContainerCounter = c;
	}
	
	public void setDecisionTrackerContainerSize(int s) {
		decisionTrackerContainerSize = s;
	}

	public void setDecisionTrackerContainer(Vector<String> v) {
		decisionTrackerContainer = v;
	}

	public void setUsbongAnswerContainer(Vector<String> v) {
		usbongAnswerContainer = v;
	}

	public void setUsbongNodeContainer(Vector<String> v) {
		usbongNodeContainer = v;
	}

    public String getMyTree() {
		return myTree;
	}

    public String getCurrUsbongNode() {
		return currUsbongNode;
	}

	public String getNextUsbongNodeIfYes() {
		return nextUsbongNodeIfYes;		
	}

	public String getNextUsbongNodeIfNo() {
		return nextUsbongNodeIfNo;		
	}

	public int getUsbongAnswerContainerCounter() {
		return usbongAnswerContainerCounter;
	}

	public int getUsbongNodeContainerCounter() {
		return usbongNodeContainerCounter;
	}
	
	public int getDecisionTrackerContainerSize() {
		return decisionTrackerContainerSize;
	}

	public Vector<String> getDecisionTrackerContainer() {
		return decisionTrackerContainer;
	}

	public Vector<String> getUsbongAnswerContainer() {
		return usbongAnswerContainer;
	}

	public Vector<String> getUsbongNodeContainer() {
		return usbongNodeContainer;
	}
	
	public static final Parcelable.Creator<UdteaObject> CREATOR = new Parcelable.Creator<UdteaObject>() {
		public UdteaObject createFromParcel(Parcel in) {
			return new UdteaObject(in);
		}
		
		public UdteaObject[] newArray(int size) {
			return new UdteaObject[size];
		}
	};
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(myTree);
		dest.writeString(currUsbongNode);
		dest.writeString(nextUsbongNodeIfYes); 
		dest.writeString(nextUsbongNodeIfNo);
		dest.writeInt(usbongAnswerContainerCounter);
		dest.writeInt(usbongNodeContainerCounter);
		dest.writeInt(decisionTrackerContainerSize);		
    	for (int i=0; i<decisionTrackerContainerSize; i++) {
    		dest.writeString((decisionTrackerContainer.elementAt(i)));
    	}
		dest.writeInt(usbongAnswerContainerSize);		
    	for (int i=0; i<usbongAnswerContainerSize; i++) {
    		dest.writeString((usbongAnswerContainer.elementAt(i)));
    	}
		dest.writeInt(usbongNodeContainerSize);		
    	for (int i=0; i<usbongNodeContainerSize; i++) {
    		dest.writeString((usbongNodeContainer.elementAt(i)));
    	}
	}
	
	private UdteaObject(Parcel in) {
		myTree = in.readString();
		currUsbongNode = in.readString();
		nextUsbongNodeIfYes = in.readString();
		nextUsbongNodeIfNo = in.readString();
		usbongAnswerContainerCounter = in.readInt();
		usbongNodeContainerCounter = in.readInt();
		decisionTrackerContainerSize = in.readInt();
    	for (int i=0; i<decisionTrackerContainerSize; i++) {
    		decisionTrackerContainer.add(in.readString());
    	}
    	usbongAnswerContainerSize = in.readInt();
    	for (int i=0; i<usbongAnswerContainerSize; i++) {
    		usbongAnswerContainer.add(in.readString());
    	}
    	usbongNodeContainerSize = in.readInt();
    	for (int i=0; i<usbongNodeContainerSize; i++) {
    		usbongNodeContainer.add(in.readString());
    	}
	}
}
