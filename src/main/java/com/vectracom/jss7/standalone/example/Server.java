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
import org.mobicents.protocols.ss7.m3ua.parameter.RoutingContext;
import org.mobicents.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.mobicents.protocols.ss7.map.MAPParameterFactoryImpl;
import org.mobicents.protocols.ss7.map.MAPStackImpl;
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
import org.mobicents.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.mobicents.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.mobicents.protocols.ss7.map.api.primitives.IMEI;
import org.mobicents.protocols.ss7.map.api.primitives.IMSI;
import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;
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
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSMSClass;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GeodeticInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GeographicalInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationNumberMap;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.NotReachableReason;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberStateChoice;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.UserCSGInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.mobicents.protocols.ss7.map.primitives.ISDNAddressStringImpl;
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

public class Server extends AbstractBase{
	private static Logger logger = Logger.getLogger(Server.class);

	// SCTP
	private ManagementImpl sctpManagement;
//	private NettySctpManagementImpl sctpManagement;
	
	private AssociationImpl serverAssociation = null;

	// M3UA
	private M3UAManagementImpl serverM3UAMgmt;
	// SCCP
	private SccpStackImpl sccpStack;
	// TCAP
	private TCAPStack tcapStack;

	// MAP
	private MAPStackImpl mapStack;
	private MAPProvider mapProvider;

	
	public void initSCTP(IpChannelType ipChannelType) throws Exception {
	//	logger.info("Initializing SCTP Stack ....");
		
		if (Configuration.Serverside == true) {
		this.sctpManagement = new ManagementImpl("Server");
//		this.sctpManagement = new NettySctpManagementImpl("Server");
//		this.sctpManagement.setSingleThread(true);
		this.sctpManagement.start();
		this.sctpManagement.removeAllResourses();

		this.sctpManagement.setConnectDelay(10000);
		// 1. Create SCTP Server
		sctpManagement.addServer(SERVER_NAME, SERVER_IP, SERVER_PORT, ipChannelType, null);

		// 2. Create SCTP Server Association
		this.serverAssociation = sctpManagement
		.addServerAssociation(CLIENT_IP, CLIENT_PORT, SERVER_NAME, SERVER_ASSOCIATION_NAME, ipChannelType);
//		serverAssociation.setAssociationListener(new ServerAssociationListener());
		
		// 3. Start Server
		sctpManagement.startServer(SERVER_NAME);
		}
		else {
			this.sctpManagement = new ManagementImpl("Server");
			this.sctpManagement.setSingleThread(true);
			this.sctpManagement.start();
			this.sctpManagement.setConnectDelay(5000);
			this.sctpManagement.removeAllResourses();

			// 1. Create SCTP Association
			this.serverAssociation = this.serverAssociation = sctpManagement.addAssociation(SERVER_IP, SERVER_PORT, CLIENT_IP, CLIENT_PORT, 
					SERVER_ASSOCIATION_NAME, ipChannelType, null);		
		}
	//	logger.info("Initialized SCTP Stack ....");
}	

	public AssociationImpl getAssociation() {
		return this.serverAssociation;
	}

	public ManagementImpl getManagement() {
		return this.sctpManagement;
	}

	public M3UAManagementImpl getMtp3Management() {
		return this.serverM3UAMgmt;
	}
	
	private void initM3UA() throws Exception {
	//	logger.info("Initializing M3UA Stack ....");
		
		if (Configuration.Serverside == true) {
		this.serverM3UAMgmt = new M3UAManagementImpl("Server-Mtp3UserPart", null);
		this.serverM3UAMgmt.setTransportManagement(this.sctpManagement);
	 //this.serverM3UAMgmt.setDeliveryMessageThreadCount(DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT);
		
		// do not start here so early
//		this.serverM3UAMgmt.start();
//		this.serverM3UAMgmt.removeAllResourses();

		// Step 1 : Create App Server

		RoutingContext rc = factory.createRoutingContext(new long[] { ROUTING_CONTEXT });
		TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
		this.serverM3UAMgmt.createAs("RAS1", Functionality.SGW, ExchangeType.SE, IPSPType.CLIENT, rc,
				trafficModeType, 1, null);

		// Step 2 : Create ASP
		this.serverM3UAMgmt.createAspFactory("RASP1", SERVER_ASSOCIATION_NAME);

		// Step3 : Assign ASP to AS
		this.serverM3UAMgmt.assignAspToAs("RAS1", "RASP1");

		// Step 4: Add Route. Remote point code is 2
		this.serverM3UAMgmt.addRoute(CLIENT_SPC, -1, -1, "RAS1");
	   }
		else {
			// client side configuration of M3UA
			this.serverM3UAMgmt = new M3UAManagementImpl("Server-Mtp3UserPart", null);
			this.serverM3UAMgmt.setTransportManagement(this.sctpManagement);
			this.serverM3UAMgmt.start();
			this.serverM3UAMgmt.removeAllResourses();

						// m3ua as create rc <rc> <ras-name>
			RoutingContext rc = factory.createRoutingContext(new long[] { ROUTING_CONTEXT });
			TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
			this.serverM3UAMgmt.createAs("RAS1", Functionality.IPSP, ExchangeType.SE, IPSPType.CLIENT, rc, 
							trafficModeType, 1, null);

						// Step 2 : Create ASP
			this.serverM3UAMgmt.createAspFactory("RASP1", SERVER_ASSOCIATION_NAME);
					// Step3 : Assign ASP to AS
			AspImpl asp = this.serverM3UAMgmt.assignAspToAs("RAS1", "RASP1");
				// Step 4: Add Route. Remote point code is 2
			serverM3UAMgmt.addRoute(CLIENT_SPC, -1, -1, "RAS1");	
		}
		// now start it here
		this.serverM3UAMgmt.start();

		//logger.info("Initialized M3UA Stack ....");
   }	

	protected SccpStackImpl getSccpStack() {
		return this.sccpStack;
	}

	
	private void initSCCP() throws Exception {
		//logger.debug("Initializing SCCP Stack ....");
		this.sccpStack = new SccpStackImpl("Server-SccpStack");
		this.sccpStack.setMtp3UserPart(1, this.serverM3UAMgmt);

		this.sccpStack.start();
		this.sccpStack.removeAllResourses();

		 this.sccpStack.getSccpResource().addRemoteSpc(1, CLIENT_SPC, 0, 0);
         this.sccpStack.getSccpResource().addRemoteSsn(1, CLIENT_SPC,  CLIENT_SSN, 0, false);

         this.sccpStack.getRouter().addMtp3ServiceAccessPoint(1, 1, SERVER_SPC, NETWORK_INDICATOR, 0);
         this.sccpStack.getRouter().addMtp3Destination(1, 1, CLIENT_SPC, CLIENT_SPC, 0, 255, 255);
         // configure gtt address
         EncodingScheme ec = new BCDEvenEncodingScheme();
         GlobalTitle gt = null;
         gt = new GlobalTitle0100Impl("-", 0,  ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
                 NatureOfAddress.INTERNATIONAL);

         SccpAddress localAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, CLIENT_SPC, 0 );
         this.sccpStack.getRouter().addRoutingAddress(1, localAddress);
         gt = new GlobalTitle0100Impl("*", 0,  ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
                 NatureOfAddress.INTERNATIONAL);
      SccpAddress pattern = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, CLIENT_SPC, 0 );
         this.sccpStack.getRouter().addRule(1, RuleType.SOLITARY, LoadSharingAlgorithm.Undefined, OriginationType.LOCAL, pattern, "K", 1, -1, null, 0);

         gt = new GlobalTitle0100Impl("-", 0,  ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
                 NatureOfAddress.INTERNATIONAL);
         SccpAddress remoteAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, SERVER_SPC, 0 );
         this.sccpStack.getRouter().addRoutingAddress(2, remoteAddress);
         gt = new GlobalTitle0100Impl("*", 0,  ec, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
                 NatureOfAddress.INTERNATIONAL);
       pattern = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, SERVER_SPC, 0 );
         this.sccpStack.getRouter().addRule(2, RuleType.SOLITARY, LoadSharingAlgorithm.Undefined, OriginationType.REMOTE, pattern, "K", 2, -1, null, 0);

		
		
		//logger.debug("Initialized SCCP Stack ....");
	}

	private void initTCAP() throws Exception {
	//	logger.debug("Initializing TCAP Stack ....");
		this.tcapStack = new TCAPStackImpl("Server-TcapStack", this.sccpStack.getSccpProvider(), SERVER_SSN);
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
		//logger.debug("Initializing MAP Stack ....");
		this.mapStack = new MAPStackImpl("Server-MapStack", this.tcapStack.getProvider());
	
		this.mapProvider = this.mapStack.getMAPProvider();

		this.mapProvider.addMAPDialogListener( this);

        this.mapProvider.getMAPServiceMobility().addMAPServiceListener(this);
        this.mapProvider.getMAPServiceMobility().acivate();
	
		
		this.mapStack.start();
		//logger.debug("Initialized MAP Stack ....");
	}

	protected void initializeStack(IpChannelType ipChannelType) throws Exception {

		this.initSCTP(ipChannelType);

		this.initM3UA();

		this.initSCCP();

		this.initTCAP();
		this.initMAP();

		// 7. Start ASP
		serverM3UAMgmt.startAsp("RASP1");

		logger.debug("[[[[[[[[[[    Started Server (HLR)    ]]]]]]]]]]");
	}
	
	
	
	public static void main(String[] args) {
		logger.info("Hello Server(HLR)");
		final Server server = new Server();
		
		try {
			server.initializeStack(IpChannelType.SCTP);
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


	public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest atiReq) {

		logger.info(String.format("onAnyTimeInterrogationRequest for DialogId=%d",atiReq.getMAPDialog().getLocalDialogId()));
		
		try {
			   long invokeId = atiReq.getInvokeId();
	            MAPDialogMobility mapDialogMobility = atiReq.getMAPDialog();
	            mapDialogMobility.setUserObject(invokeId);

	            MAPParameterFactoryImpl mapFactory = new MAPParameterFactoryImpl();

	            // Create Subscriber Information parameters including Location Information and Subscriber State
	            // for concerning MAP operation
	            CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength = mapFactory
	                    .createCellGlobalIdOrServiceAreaIdFixedLength(410, 03, 23, 369);
	            CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = mapFactory
	                    .createCellGlobalIdOrServiceAreaIdOrLAI(cellGlobalIdOrServiceAreaIdFixedLength);
	            ISDNAddressString vlrNumber = new ISDNAddressStringImpl(AddressNature.international_number,
	                    org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, "5982123007");
	            ISDNAddressString mscNumber = new ISDNAddressStringImpl(AddressNature.international_number,
	                    org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, "5982123007");
	            Integer ageOfLocationInformation = 0; // ageOfLocationInformation
	            GeographicalInformation geographicalInformation = null;
	            LocationNumberMap locationNumber = null;
	            MAPExtensionContainer mapExtensionContainer = null;
	            LSAIdentity selectedLSAId = null;
	            GeodeticInformation geodeticInformation = null;
	            boolean currentLocationRetrieved = false;
	            boolean saiPresent = false;
	            LocationInformationEPS locationInformationEPS = null;
	            UserCSGInformation userCSGInformation = null;
	            LocationInformationGPRS locationInformationGPRS = null;
	            PSSubscriberState psSubscriberState = null;
	            IMEI imei = null;
	            MSClassmark2 msClassmark2 = null;
	            GPRSMSClass gprsMSClass = null;
	            MNPInfoRes mnpInfoRes = null;
	            SubscriberStateChoice subscriberStateChoice = SubscriberStateChoice.assumedIdle; // 0=assumedIdle, 1=camelBusy, 2=notProvidedFromVLR
	            NotReachableReason notReachableReason = null;

	            LocationInformation locationInformation = mapFactory.createLocationInformation(ageOfLocationInformation,
	                    geographicalInformation, vlrNumber, locationNumber, cellGlobalIdOrServiceAreaIdOrLAI, mapExtensionContainer,
	                    selectedLSAId, mscNumber, geodeticInformation, currentLocationRetrieved, saiPresent, locationInformationEPS,
	                    userCSGInformation);

	            SubscriberState subscriberState = mapFactory.createSubscriberState(subscriberStateChoice, notReachableReason);

	            SubscriberInfo subscriberInfo = mapFactory.createSubscriberInfo(locationInformation, subscriberState,
	                    mapExtensionContainer, locationInformationGPRS, psSubscriberState, imei, msClassmark2, gprsMSClass,
	                    mnpInfoRes);

	            mapDialogMobility.addAnyTimeInterrogationResponse(invokeId, subscriberInfo, mapExtensionContainer);

	            // This will initiate the TC-BEGIN with INVOKE component
	            mapDialogMobility.close(false);

		} catch (MAPException mapException) {
			 logger.error("MAP Exception while processing AnyTimeInterrogationRequest ", mapException);
		} catch (Exception e) {
			  logger.error("Exception while processing AnyTimeInterrogationRequest ", e);
		}
		
	}


	public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse arg0) {
		// TODO Auto-generated method stub
		
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
		
		  // HLR should not be set as serverside=false because client can not multiclient sctp as server
		  public static final boolean Serverside = true;
		}
	
}
