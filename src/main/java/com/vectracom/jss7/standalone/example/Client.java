package com.vectracom.jss7.standalone.example;

import org.apache.log4j.Logger;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.AssociationImpl;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.mobicents.protocols.ss7.indicator.NatureOfAddress;
import org.mobicents.protocols.ss7.indicator.RoutingIndicator;
import org.mobicents.protocols.ss7.m3ua.ExchangeType;
import org.mobicents.protocols.ss7.m3ua.Functionality;
import org.mobicents.protocols.ss7.m3ua.IPSPType;
import org.mobicents.protocols.ss7.m3ua.impl.AspImpl;
import org.mobicents.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.mobicents.protocols.ss7.m3ua.parameter.NetworkAppearance;
import org.mobicents.protocols.ss7.m3ua.parameter.RoutingContext;
import org.mobicents.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.mobicents.protocols.ss7.map.MAPStackImpl;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContext;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextName;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.MAPMessage;
import org.mobicents.protocols.ss7.map.api.MAPProvider;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.mobicents.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
import org.mobicents.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.mobicents.protocols.ss7.map.api.primitives.AddressNature;
import org.mobicents.protocols.ss7.map.api.primitives.AddressString;
import org.mobicents.protocols.ss7.map.api.primitives.IMSI;
import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan;
import org.mobicents.protocols.ss7.map.api.primitives.SubscriberIdentity;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.ForwardCheckSSIndicationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.ResetRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.imei.CheckImeiRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.imei.CheckImeiResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeRequest_Mobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeResponse_Mobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.mobicents.protocols.ss7.sccp.LoadSharingAlgorithm;
import org.mobicents.protocols.ss7.sccp.OriginationType;
import org.mobicents.protocols.ss7.sccp.RuleType;
import org.mobicents.protocols.ss7.sccp.impl.SccpStackImpl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.mobicents.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.mobicents.protocols.ss7.sccp.parameter.EncodingScheme;
import org.mobicents.protocols.ss7.sccp.parameter.GlobalTitle;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;
import org.mobicents.protocols.ss7.tcap.TCAPStackImpl;
import org.mobicents.protocols.ss7.tcap.api.TCAPStack;
import org.mobicents.protocols.ss7.tcap.asn.ApplicationContextName;
import org.mobicents.protocols.ss7.tcap.asn.comp.Problem;

public class Client extends AbstractBase {
	private static Logger logger = Logger.getLogger(Client.class);

	// SCTP
	private ManagementImpl sctpManagement;
//	private NettySctpManagementImpl sctpManagement;
	
	private AssociationImpl clientAssociation = null;
	// M3UA
	private M3UAManagementImpl M3UAMgmt;

	// SCCP
	private SccpStackImpl sccpStack;
	// TCAP
	private TCAPStack tcapStack;
	// MAP
	private MAPStackImpl mapStack;
	private MAPProvider mapProvider;

	protected void addAssociation(String clientIp, int clientPort, String serverIp, int serverPort, String ipChannelType) {
		this.CLIENT_IP = clientIp;
		this.CLIENT_PORT = clientPort;
		this.SERVER_IP = serverIp;
		this.SERVER_PORT = serverPort;
		if (ipChannelType.equals("sctp")) {
			this.ipChannelType = IpChannelType.SCTP;
		}
		else if (ipChannelType.equals("tcp")) {
			this.ipChannelType = IpChannelType.TCP;
		}
		else {
			throw new IllegalArgumentException("unknown ipChannelType");
		}
	}
	
	public void initSCTP() throws Exception {

		if (CLIENT_IP == null)
			throw new NullPointerException("CLIENT_IP is null");
		if (SERVER_IP == null)
			throw new NullPointerException("SERVER_IP is null");

		if (Configuration.Serverside == false) {
			this.sctpManagement = new ManagementImpl("Client");
			this.sctpManagement.setSingleThread(true);
			this.sctpManagement.start();
			this.sctpManagement.setConnectDelay(10000);
			this.sctpManagement.removeAllResourses();

			 // 1. Create SCTP Association
			this.clientAssociation = sctpManagement.addAssociation(CLIENT_IP, CLIENT_PORT, SERVER_IP, SERVER_PORT, 
					CLIENT_ASSOCIATION_NAME, ipChannelType, null);
		}
		else  {
			this.sctpManagement = new ManagementImpl("Client");

			this.sctpManagement.start();
			this.sctpManagement.removeAllResourses();
			this.sctpManagement.setConnectDelay(10000);
			// 1. Create SCTP Server
			sctpManagement.addServer(CLIENT_NAME, CLIENT_IP, CLIENT_PORT, ipChannelType, null);
			// 2. Create SCTP Server Association
			this.clientAssociation = sctpManagement
			.addServerAssociation(SERVER_IP, SERVER_PORT, CLIENT_NAME, CLIENT_ASSOCIATION_NAME, ipChannelType);
			
			// 3. Start Server
			sctpManagement.startServer(CLIENT_NAME);		
		}
		
	}

	public void stopSCTP() throws Exception {
		if (Configuration.Serverside == false) {
			this.sctpManagement.removeAllResourses();
			this.sctpManagement.stop();
		}
		else {
			this.sctpManagement.removeAllResourses();
			this.sctpManagement.stop();
		}
	}
	
	public AssociationImpl getAssociation() {
		return this.clientAssociation;
	}
	
	public ManagementImpl getManagement() {
		return this.sctpManagement;
	}
	
	public M3UAManagementImpl getMtp3Management() {
		return this.M3UAMgmt;
	}
	private void initM3UA() throws Exception {
		//logger.debug("Initializing M3UA Stack ....");
		this.M3UAMgmt = new M3UAManagementImpl("Client-Mtp3UserPart", "Restcomm-Jss7");
		this.M3UAMgmt.setTransportManagement(this.sctpManagement);
		// do not start here so early
//		this.M3UAMgmt.start();
//		this.M3UAMgmt.removeAllResourses();

		// m3ua as create rc <rc> <ras-name>
		RoutingContext rc = factory.createRoutingContext(new long[] { ROUTING_CONTEXT });
		NetworkAppearance na = factory.createNetworkAppearance(0l);
		
		if (Configuration.Serverside == true ) {

			TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
			this.M3UAMgmt
			.createAs("AS1", Functionality.SGW, ExchangeType.SE, IPSPType.CLIENT, rc,
				trafficModeType, 1, na);
		}
		else {
			
				// client side configuration of M3UA
				// m3ua as create rc <rc> <ras-name>
				TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
				this.M3UAMgmt.createAs("AS1", Functionality.IPSP, ExchangeType.SE, 
						IPSPType.CLIENT, 
						rc, 
						trafficModeType, 
						1, na);
		
		}
		// Step 2 : Create ASP
		this.M3UAMgmt.createAspFactory("ASP1", CLIENT_ASSOCIATION_NAME);
		// Step3 : Assign ASP to AS
		AspImpl asp = this.M3UAMgmt.assignAspToAs("AS1", "ASP1");
		// Step 4: Add Route. Remote point code is 2
		M3UAMgmt.addRoute(SERVER_SPC, -1, -1, "AS1");
		this.M3UAMgmt.start();
	
		//logger.debug("Initialized M3UA Stack ....");
	}
	
	protected SccpStackImpl getSccpStack() {
		return this.sccpStack;
	}
	
	private void initSCCP() throws Exception {
		//logger.debug("Initializing SCCP Stack ....");
		this.sccpStack = new SccpStackImpl("Client-SccpStack");
		this.sccpStack.setMtp3UserPart(1, this.M3UAMgmt);

		this.sccpStack.start();
		this.sccpStack.removeAllResourses();

		 this.sccpStack.getSccpResource().addRemoteSpc(1, SERVER_SPC, 0, 0);
         this.sccpStack.getSccpResource().addRemoteSsn(1, SERVER_SPC,  8, 0, false);
         this.sccpStack.getSccpResource().addRemoteSsn(2, SERVER_SPC,  6, 0, false);

         this.sccpStack.getRouter().addMtp3ServiceAccessPoint(1, 1, CLIENT_SPC, NETWORK_INDICATOR, 0);
         this.sccpStack.getRouter().addMtp3Destination(1, 1, SERVER_SPC, SERVER_SPC, 0, 255, 255);
         // configure gtt address
         GlobalTitle gt = null;
         EncodingScheme ec = new BCDEvenEncodingScheme();
         
         gt = new GlobalTitle0100Impl("000", 0, ec,  org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, NatureOfAddress.INTERNATIONAL);
         SccpAddress localAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, SERVER_SPC, 0 );
         this.sccpStack.getRouter().addRoutingAddress(1, localAddress);
         gt = new GlobalTitle0100Impl("*", 0, ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, NatureOfAddress.INTERNATIONAL);            
         SccpAddress pattern = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, SERVER_SPC, 0 );
         this.sccpStack.getRouter().addRule(1, RuleType.SOLITARY, LoadSharingAlgorithm.Undefined, OriginationType.LOCAL, pattern, "K", 1, -1, null, 0);

         
         
         
         gt = new GlobalTitle0100Impl("000", 0, ec,  org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, NatureOfAddress.INTERNATIONAL);
         SccpAddress remoteAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, CLIENT_SPC, 0 );
         this.sccpStack.getRouter().addRoutingAddress(2, remoteAddress);
//         gt = new GlobalTitle0001Impl("*", nai);            
         gt = new GlobalTitle0100Impl("*", 0, ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, NatureOfAddress.INTERNATIONAL);            
         pattern = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, CLIENT_SPC, 0 );
         this.sccpStack.getRouter().addRule(2, RuleType.SOLITARY, LoadSharingAlgorithm.Undefined, OriginationType.REMOTE, pattern, "K", 2, -1, null, 0);

		
		
	//	logger.debug("Initialized SCCP Stack ....");
	}
	
	private void initTCAP() throws Exception {
	//	logger.debug("Initializing TCAP Stack ....");
		this.tcapStack = new TCAPStackImpl("Client-TcapStack", this.sccpStack.getSccpProvider(), CLIENT_SSN);
		this.tcapStack.start();
		this.tcapStack.setDialogIdleTimeout(60000);
		this.tcapStack.setInvokeTimeout(30000);
		this.tcapStack.setMaxDialogs(2000);
	//	logger.debug("Initialized TCAP Stack ....");
	}

	protected MAPStackImpl getMapStack() {
		return this.mapStack;
	}
	private void initMAP() throws Exception {
	//	logger.debug("Initializing MAP Stack ....");
		this.mapStack = new MAPStackImpl("Client-MapStack", this.tcapStack.getProvider());
	
		this.mapProvider = this.mapStack.getMAPProvider();

		this.mapProvider.addMAPDialogListener( this);


        this.mapProvider.getMAPServiceMobility().addMAPServiceListener(this);
        this.mapProvider.getMAPServiceMobility().acivate();
	
		
		this.mapStack.start();
	//	logger.debug("Initialized MAP Stack ....");
	}
	
	
	protected void initializeStack() throws Exception {

		this.initSCTP();

		this.initM3UA();
		this.initSCCP();
		this.initTCAP();
		this.initMAP();
		
		// FInally start ASP
		this.M3UAMgmt.startAsp("ASP1");

	}	
	
	protected void stop() throws Exception {
		this.mapStack.stop();
		this.tcapStack.stop();
		this.sccpStack.stop();
		this.M3UAMgmt.stop();
		this.sctpManagement.stop();
	}
	
	  protected void initiateATI() throws MAPException {
		    
		    //	try {
		     //   	this.mapStack.getTCAPStack().setDialogIdleTimeout(3000);
			//		this.mapStack.getTCAPStack().setInvokeTimeout(3000);
			//	} catch (Exception e) {
					// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}

		    	EncodingScheme es = new BCDEvenEncodingScheme();
		      	GlobalTitle GTHLR_from_msisdn = new GlobalTitle0100Impl(
		      			"923455687890", 0, es, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
		      			NatureOfAddress.INTERNATIONAL );

		     	GlobalTitle gtSCF = new GlobalTitle0100Impl(
		     			"923211234567", 0, es, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, 
		      			NatureOfAddress.INTERNATIONAL );
		   
				// ufone message uses gsmSCF GT 923330055101
		   	  AddressString origRef = this.mapProvider.getMAPParameterFactory()
		              .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
			// ufone message uses msisdn in destaddress
		      AddressString destRef = this.mapProvider.getMAPParameterFactory()
		              .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
		     	
//		    	SccpAddress cgpa = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, gtSCF, CLIENT_SPC, CLIENT_SSN);
		    	SccpAddress cgpa = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gtSCF, CLIENT_SPC, CLIENT_SSN);

		    	
		    	//   	SccpAddress cgpa = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, CLIENT_SPC, SSN);
		    	SccpAddress cdpa = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, GTHLR_from_msisdn,
		    			SERVER_SPC, 6);
		 //   	SccpAddress cdpa = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, gtHLR, SERVER_SPC, 6);
		    	
		    	MAPDialogMobility mapDialog = this.mapProvider.getMAPServiceMobility().createNewDialog(
		    			MAPApplicationContext.getInstance(MAPApplicationContextName.anyTimeEnquiryContext,
		    			MAPApplicationContextVersion.version3),	cgpa, origRef, cdpa, destRef);
		    
		    	ISDNAddressString msisdn;
		    	SubscriberIdentity si;
		    	ISDNAddressString gsmscfaddress;
		    	MAPExtensionContainer c;
		   // 	c.setPrivateExtensionList(null);
		    	RequestedInfo inf = this.mapProvider.getMAPParameterFactory().createRequestedInfo(true, false, null, false, null, false, false, false);
		    	
		    	msisdn = this.mapProvider.getMAPParameterFactory().createISDNAddressString(
		    			AddressNature.international_number, NumberingPlan.ISDN, "923455681234");
		    	si = this.mapProvider.getMAPParameterFactory().createSubscriberIdentity(msisdn);
		    	
		    	gsmscfaddress = this.mapProvider.getMAPParameterFactory().createISDNAddressString(
		    			AddressNature.international_number, NumberingPlan.ISDN, "923123456789");
		    
		    	mapDialog.addAnyTimeInterrogationRequest(si, inf, gsmscfaddress, null);
		    
		        mapDialog.send();
	}
	  
	public static void main(String[] args) {

		logger.info("Hello Client");
		Client client = new Client();
		client.addAssociation("127.0.0.1", 8012, "127.0.0.1", 8011, "sctp");
		try {
			client.initializeStack();
			Thread.sleep(10000);
			client.initiateATI();
			Thread.sleep(20000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void onDialogAccept(MAPDialog arg0, MAPExtensionContainer arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogClose(MAPDialog arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogDelimiter(MAPDialog arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogNotice(MAPDialog arg0, MAPNoticeProblemDiagnostic arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogProviderAbort(MAPDialog arg0, MAPAbortProviderReason arg1, MAPAbortSource arg2,
			MAPExtensionContainer arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogReject(MAPDialog arg0, MAPRefuseReason arg1, ApplicationContextName arg2,
			MAPExtensionContainer arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogRelease(MAPDialog arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogRequest(MAPDialog arg0, AddressString arg1, AddressString arg2, MAPExtensionContainer arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogRequestEricsson(MAPDialog arg0, AddressString arg1, AddressString arg2, IMSI arg3,
			AddressString arg4) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogTimeout(MAPDialog arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDialogUserAbort(MAPDialog arg0, MAPUserAbortChoice arg1, MAPExtensionContainer arg2) {
		// TODO Auto-generated method stub
		
	}

	public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse response) {
		logger.info(response.getSubscriberInfo() );
		
	}

	public void onAuthenticationFailureReportRequest(AuthenticationFailureReportRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onAuthenticationFailureReportResponse(AuthenticationFailureReportResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onCancelLocationRequest(CancelLocationRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onCancelLocationResponse(CancelLocationResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onCheckImeiRequest(CheckImeiRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onCheckImeiResponse(CheckImeiResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDeleteSubscriberDataRequest(DeleteSubscriberDataRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onDeleteSubscriberDataResponse(DeleteSubscriberDataResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onForwardCheckSSIndicationRequest(ForwardCheckSSIndicationRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onPurgeMSRequest(PurgeMSRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onPurgeMSResponse(PurgeMSResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onResetRequest(ResetRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRestoreDataRequest(RestoreDataRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRestoreDataResponse(RestoreDataResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onSendAuthenticationInfoRequest(SendAuthenticationInfoRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onSendAuthenticationInfoResponse(SendAuthenticationInfoResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onSendIdentificationRequest(SendIdentificationRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onSendIdentificationResponse(SendIdentificationResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onUpdateGprsLocationRequest(UpdateGprsLocationRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onUpdateGprsLocationResponse(UpdateGprsLocationResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onUpdateLocationRequest(UpdateLocationRequest arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onUpdateLocationResponse(UpdateLocationResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onErrorComponent(MAPDialog arg0, Long arg1, MAPErrorMessage arg2) {
		// TODO Auto-generated method stub
		
	}

	public void onInvokeTimeout(MAPDialog arg0, Long arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onMAPMessage(MAPMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onRejectComponent(MAPDialog arg0, Long arg1, Problem arg2, boolean arg3) {
		// TODO Auto-generated method stub
		
	} 

	public final class Configuration {
		  //set to false to allow compiler to identify and eliminate
		  //unreachable code
		  public static final boolean Serverside = false;
		}
	
}
