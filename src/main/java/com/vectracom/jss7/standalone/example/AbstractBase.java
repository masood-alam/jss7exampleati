package com.vectracom.jss7.standalone.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.mobicents.protocols.ss7.map.api.MAPDialogListener;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPServiceMobilityListener;

public abstract class AbstractBase implements MAPDialogListener, MAPServiceMobilityListener {
	private static final Logger logger = Logger.getLogger("map.test");
	protected static final String LOG_FILE_NAME = "log.file.name";
	protected static String logFileName = "maplog.txt";

	// MTP Details
	protected final int CLIENT_SPC = 2;
	protected final int SERVER_SPC = 1;
	protected final int NETWORK_INDICATOR = 2;
	protected final int SERVICE_INIDCATOR = 3; // SCCP
	protected final int CLIENT_SSN = 147;//8;	// for USSD request
	protected final int SERVER_SSN = 6; // HLR

	// M3UA details
//	protected final String CLIENT_IP = "192.168.56.1";
//	protected final String CLIENT_IP = "172.17.0.1";
	protected String CLIENT_IP = null;//"127.0.0.1";
	protected int CLIENT_PORT = 8012;

//	protected final String SERVER_IP = "192.168.56.1";
//	protected final String SERVER_IP = "172.17.0.2";
//	protected final String SERVER_IP = "172.17.0.2";
	protected String SERVER_IP = null;//"127.0.0.1";
	protected int SERVER_PORT = 8011;
	
	protected IpChannelType ipChannelType=IpChannelType.SCTP;

	protected final int ROUTING_CONTEXT = 101;

	// protected static int DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;	
	protected final String SERVER_ASSOCIATION_NAME = "serverAssociation";
	protected final String CLIENT_ASSOCIATION_NAME = "clientAssociation";

	protected final String SERVER_NAME = "testserver";
	protected final String CLIENT_NAME = "testclient";

	protected final ParameterFactoryImpl factory = new ParameterFactoryImpl();
	protected AbstractBase() {
		init();
	}

	public void init() {
		try {

			InputStream inStreamLog4j = AbstractBase.class.getResourceAsStream("/log4j.properties");

			System.out.println("Input Stream = " + inStreamLog4j);

			Properties propertiesLog4j = new Properties();
			try {
				propertiesLog4j.load(inStreamLog4j);
				PropertyConfigurator.configure(propertiesLog4j);
			} catch (IOException e) {
				e.printStackTrace();
				BasicConfigurator.configure();
			}

			logger.debug("log4j configured");

			String lf = System.getProperties().getProperty(LOG_FILE_NAME);
			if (lf != null) {
				logFileName = lf;
			}

			// If already created a print writer then just use it.
			try {
				logger.addAppender(new FileAppender(new SimpleLayout(), logFileName));
			} catch (FileNotFoundException fnfe) {

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}

	}

}
