package com.vectracom.jss7.standalone.example;

import org.apache.log4j.Logger;
import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.AssociationListener;
import org.mobicents.protocols.api.PayloadData;
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
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


import junit.framework.Assert;

public class ClientTest {
	private static Logger logger = Logger.getLogger(ClientTest.class);
	
	boolean clientAssociationUp = false;
	boolean clientM3UAUp = false;

	Client client = null;
	Server server = null;
	private SignallingPointStatus clientSignallingPointStatus = null;
	
	private AnyTimeInterrogationResponse anyTimeInterrogationResponse = null;

	@BeforeMethod
	public void  setUp() throws Exception {
		System.out.println("setup");
		  this.clientAssociationUp = false;
		  this.clientM3UAUp = false;
		  client = new Client();
		  client.addAssociation("127.0.0.1", 8012, "127.0.0.1", 8011, "sctp");
		  client.initializeStack();
		  server = new Server();
		  server.addAssociation("127.0.0.1", 8011, "127.0.0.1", 8012, "sctp");
		  server.initializeStack();
	
	}
	
	@AfterMethod
	public void tearDown() throws Exception {
		System.out.println("setdown");
		client.stop();
		server.stop();
	}
	
  @Test (enabled=false)
  public void testLocalAssociationConnection() throws Exception {
	  
		// there is only one listener possible in association !!!
		client.getAssociation().setAssociationListener(new ClientAssociationListenerImpl());
	//	client.getManagement().startAssociation("clientAssociation");
	
		//server.getManagement().startAssociation("serverAssociation");
		while(clientAssociationUp != true) {
			Thread.sleep(100);
		}
		
		System.out.println("testlocal");
		Assert.assertEquals(clientAssociationUp, true);
	  
  }

  @Test (enabled=false)
  public void testLocalAssociationConnection2() throws Exception {
	  
		// there is only one listener possible in association !!!
		client.getAssociation().setAssociationListener(new ClientAssociationListenerImpl());
		//client.getManagement().startAssociation("clientAssociation");
	
	//	client.getManagement().startAssociation("clientAssociation");
		//server.getManagement().startAssociation("serverAssociation");
		while(clientAssociationUp != true) {
			Thread.sleep(100);
		}
				
		Assert.assertEquals(clientAssociationUp, true);
		System.out.println("testlocal2");

  }

	@Test (timeOut=30000,enabled=false)
	  public void testLocalM3UAConnection() throws Exception {
//		  Client client = new Client();
//		  client.addAssociation("127.0.0.1", 8012, "127.0.0.1", 8011, "sctp");
//		  client.initializeStack();
		client.getMtp3Management().addM3UAManagementEventListener(new ClientMtp3MangementListenerImpl());
		
//		  Server server = new Server();
//		  server.addAssociation("127.0.0.1", 8011, "127.0.0.1", 8012, "sctp");
//		  server.initializeStack();

		while(clientM3UAUp != true) {
			Thread.sleep(100);
		}
		Assert.assertEquals(clientM3UAUp, true);	
	}	

	@Test (timeOut=20000,enabled=true)
	public void testLocalSccpConnection() throws Exception {
		 // Client client = new Client();
		 // client.addAssociation("127.0.0.1", 8012, "127.0.0.1", 8011, "sctp");
		 // client.initializeStack();
		client.getSccpStack().getSccpProvider().registerSccpListener(1, new ClientSccpListenerImpl());
		
		//  Server server = new Server();
		//  server.addAssociation("127.0.0.1", 8011, "127.0.0.1", 8012, "sctp");
		//  server.initializeStack();

			while(clientSignallingPointStatus == null) {
				Thread.sleep(100);
		}
		Assert.assertEquals(clientSignallingPointStatus == SignallingPointStatus.ACCESSIBLE, true);
	}	

	@Test (timeOut=20000,enabled=false)
	  public void testLocalMapConnection() throws Exception {
		//  Client client = new Client();
		 // client.addAssociation("127.0.0.1", 8012, "127.0.0.1", 8011, "sctp");
		  //client.initializeStack();
		client.getMapStack().getMAPProvider().getMAPServiceMobility().addMAPServiceListener(new ClientMAPServiceMobilityListenerImpl());
	
		//  Server server = new Server();
		//  server.addAssociation("127.0.0.1", 8011, "127.0.0.1", 8012, "sctp");
		//  server.initializeStack();
		  
		  Thread.sleep(15000);
			client.initiateATI();

			while(anyTimeInterrogationResponse == null) {
				Thread.sleep(500);
			}
			
		}	
	
	@Test (timeOut=20000,dependsOnMethods={"testLocalSccpConnection"})
	@Parameters( { "clientIp", "clientPort", "serverIp", "serverPort", "type"} )
	  public void testMapApplication(String clientIp, int clientPort, String serverIp, int serverPort, String type) throws Exception {
		  logger.info("Param=" + clientIp +":" + clientPort + "," + serverIp + ":" + serverPort + ","+ type);
	//	  Client client = new Client();
	//	  client.addAssociation(clientIp, clientPort, serverIp, serverPort, type);
	//	  client.initializeStack();
		client.getMapStack().getMAPProvider().getMAPServiceMobility().addMAPServiceListener(new ClientMAPServiceMobilityListenerImpl());
	
	//	  Server server = new Server();
	//	  server.addAssociation(serverIp, serverPort, clientIp, clientPort, type);
	//	  server.initializeStack();
		  
		  Thread.sleep(15000);
			client.initiateATI();

			while(anyTimeInterrogationResponse == null) {
				Thread.sleep(500);
			}
			
		}	
	
	private class ClientAssociationListenerImpl implements AssociationListener {

		public void inValidStreamId(PayloadData arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onCommunicationLost(Association arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onCommunicationRestart(Association arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onCommunicationShutdown(Association arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onCommunicationUp(Association arg0, int arg1, int arg2) {
			clientAssociationUp = true;
		}

		public void onPayload(Association arg0, PayloadData arg1) {
			// TODO Auto-generated method stub
			
		}
	}
	

	private class ClientMtp3MangementListenerImpl implements M3UAManagementEventListener {

		public void onAsActive(As arg0, State arg1) {
			logger.info("client onAsActive");
			clientM3UAUp = true;
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
			logger.info("client onAsActive");
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
}
