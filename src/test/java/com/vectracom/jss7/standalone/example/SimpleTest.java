package com.vectracom.jss7.standalone.example;

import org.apache.log4j.Logger;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationListener;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.api.PayloadData;
import org.mobicents.protocols.sctp.AssociationImpl;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.mobicents.protocols.ss7.m3ua.As;
import org.mobicents.protocols.ss7.m3ua.Asp;
import org.mobicents.protocols.ss7.m3ua.AspFactory;
import org.mobicents.protocols.ss7.m3ua.M3UAManagementEventListener;
import org.mobicents.protocols.ss7.m3ua.State;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPMessage;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPServiceMobilityListener;
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
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.mobicents.protocols.ss7.sccp.RemoteSccpStatus;
import org.mobicents.protocols.ss7.sccp.SccpListener;
import org.mobicents.protocols.ss7.sccp.SignallingPointStatus;
import org.mobicents.protocols.ss7.sccp.message.SccpDataMessage;
import org.mobicents.protocols.ss7.sccp.message.SccpNoticeMessage;
import org.mobicents.protocols.ss7.tcap.asn.comp.Problem;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SimpleTest {
	private static Logger logger = Logger.getLogger(SimpleTest.class);
	
	private Client client = null;
	private Server server = null;
	
	private AssociationImpl serverAssociation = null;
	private AssociationImpl clientAssociation = null;
	
	private ManagementImpl clientManagement = null;
	
	private SignallingPointStatus clientSignallingPointStatus = null;
	private SignallingPointStatus serverSignallingPointStatus = null;

	private AnyTimeInterrogationRequest anyTimeInterrogationRequest = null;
	private AnyTimeInterrogationResponse anyTimeInterrogationResponse = null;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}
	
	public void setUp() throws Exception {
		server = new Server();
		client = new Client();
		server.addAssociation("127.0.0.1", 8011, "127.0.0.1", 8012, "sctp");
		client.addAssociation("127.0.0.1", 8012, "127.0.0.1", 8011, "sctp");
		client.initializeStack();
		server.initializeStack();

	}
	
	@Test
	public void testConnection() throws Exception {
		this.setUp();
		
		
		client.getMtp3Management().addM3UAManagementEventListener(new ClientMtp3MangementListenerImpl());
		server.getMtp3Management().addM3UAManagementEventListener(new ServerMtp3ManagementListenerImpl());
		
		client.getSccpStack().getSccpProvider().registerSccpListener(1, new ClientSccpListenerImpl());
		server.getSccpStack().getSccpProvider().registerSccpListener(1, new ServerSccpListenerImpl());
		
		client.getMapStack().getMAPProvider().getMAPServiceMobility().addMAPServiceListener(new ClientMAPServiceMobilityListenerImpl());
		server.getMapStack().getMAPProvider().getMAPServiceMobility().addMAPServiceListener(new ServerMAPServiceMobilityListenerImpl());
		
		while(clientSignallingPointStatus == null) {
				Thread.sleep(1000);
		}
		Assert.assertEquals(clientSignallingPointStatus == SignallingPointStatus.ACCESSIBLE, true);
		Assert.assertEquals(serverSignallingPointStatus == SignallingPointStatus.ACCESSIBLE, true);
		client.initiateATI();
		while(anyTimeInterrogationRequest == null) {
			Thread.sleep(500);
		}
		
		while(anyTimeInterrogationResponse == null) {
			Thread.sleep(500);
		}
			
		
		
	}
	
	private class ServerMAPServiceMobilityListenerImpl implements MAPServiceMobilityListener {

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

		public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest req) {
			anyTimeInterrogationRequest = req;
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
		
	}

	private class ClientMAPServiceMobilityListenerImpl implements MAPServiceMobilityListener {

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

		public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse res) {
			anyTimeInterrogationResponse = res;
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
		
	}
	private class ServerSccpListenerImpl implements SccpListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void onCoordRequest(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onCoordResponse(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onMessage(SccpDataMessage arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onNotice(SccpNoticeMessage arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onPcState(int pointcode, SignallingPointStatus spStatus, int arg2, RemoteSccpStatus arg3) {
//			logger.info("server onPcState" + pointcode + spStatus + arg3);
			serverSignallingPointStatus = spStatus;
		}

		public void onState(int arg0, int arg1, boolean arg2, int arg3) {
			logger.info("server onState");
		
		}
		
	}
	
	private class ClientSccpListenerImpl implements SccpListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void onCoordRequest(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onCoordResponse(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		public void onMessage(SccpDataMessage arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onNotice(SccpNoticeMessage arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onPcState(int pointcode, SignallingPointStatus spStatus, int arg2, RemoteSccpStatus arg3) {
//			logger.info("client onPcState" + pointcode + spStatus + arg3);
			clientSignallingPointStatus = spStatus;
		}

		public void onState(int arg0, int arg1, boolean arg2, int arg3) {
			// TODO Auto-generated method stub
			logger.info("client onState");
			
		}
		
	}
	
	private class ServerMtp3ManagementListenerImpl implements M3UAManagementEventListener {

		public void onAsActive(As arg0, State arg1) {
		//	logger.info("server onAsActive" + arg0.getState() + arg1);
			Assert.assertEquals(arg0.isUp(), true);

		}

		public void onAsCreated(As arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAsDestroyed(As arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAsDown(As arg0, State arg1) {
			logger.info("server onAsDown");
		
		}

		public void onAsInactive(As arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onAsPending(As arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onAspActive(Asp arg0, State arg1) {
			//logger.info("server onAspActive" + arg0.isUp() + arg1);
			Assert.assertEquals(arg0.isUp(), true);
		}

		public void onAspAssignedToAs(As arg0, Asp arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onAspDown(Asp arg0, State arg1) {
			logger.info("server onAspDown");
			
		}

		public void onAspFactoryCreated(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAspFactoryDestroyed(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAspFactoryStarted(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAspFactoryStopped(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onAspInactive(Asp arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onAspUnassignedFromAs(As arg0, Asp arg1) {
			// TODO Auto-generated method stub
			
		}

		public void onRemoveAllResources() {
			// TODO Auto-generated method stub
			
		}

		public void onServiceStarted() {
			// TODO Auto-generated method stub
			
		}

		public void onServiceStopped() {
			// TODO Auto-generated method stub
			
		}

		
	}
	
	private class ClientMtp3MangementListenerImpl implements M3UAManagementEventListener {

		public void onAsActive(As arg0, State arg1) {
		//	logger.info("client onAsActive");
			Assert.assertEquals(arg0.isUp(), true);
		}
		public void onAsCreated(As arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAsDestroyed(As arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAsDown(As arg0, State arg1) {
			
		}
		public void onAsInactive(As arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}
		public void onAsPending(As arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}
		public void onAspActive(Asp arg0, State arg1) {
			Assert.assertEquals(arg0.isUp(), true);
		}
		public void onAspAssignedToAs(As arg0, Asp arg1) {
			// TODO Auto-generated method stub
		
		}
		public void onAspDown(Asp arg0, State arg1) {			
		}
		public void onAspFactoryCreated(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAspFactoryDestroyed(AspFactory arg0) {
			// TODO Auto-generated method stub
		
		}
		public void onAspFactoryStarted(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAspFactoryStopped(AspFactory arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onAspInactive(Asp arg0, State arg1) {
			// TODO Auto-generated method stub
			
		}
		public void onAspUnassignedFromAs(As arg0, Asp arg1) {
			// TODO Auto-generated method stub
			
		}
		public void onRemoveAllResources() {
			// TODO Auto-generated method stub
			
		}
		public void onServiceStarted() {
			// TODO Auto-generated method stub
			
		}
		public void onServiceStopped() {		
		}
		
	}
	
	
		
	

}
