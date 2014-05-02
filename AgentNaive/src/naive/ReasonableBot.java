package naive;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.tools.javac.util.List;

import se.sics.isl.transport.Transportable;
import se.sics.tasim.aw.Agent;
import se.sics.tasim.aw.Message;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.demand.CampaignStats;
import tau.tac.adx.devices.Device;
import tau.tac.adx.props.AdxBidBundle;
import tau.tac.adx.props.AdxQuery;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.PublisherCatalogEntry;
import tau.tac.adx.report.adn.AdNetworkReport;
import tau.tac.adx.report.adn.MarketSegment;
import tau.tac.adx.report.demand.AdNetBidMessage;
import tau.tac.adx.report.demand.AdNetworkDailyNotification;
import tau.tac.adx.report.demand.CampaignOpportunityMessage;
import tau.tac.adx.report.demand.CampaignReport;
import tau.tac.adx.report.demand.CampaignReportKey;
import tau.tac.adx.report.demand.InitialCampaignMessage;
import tau.tac.adx.report.publisher.AdxPublisherReport;
import tau.tac.adx.report.publisher.AdxPublisherReportEntry;
import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.BankStatus;



/**
 * 
 * @authors Mariano Schain, Brendan Wallace
 * 
 */
public class ReasonableBot extends Agent {

	protected final Logger log = Logger
			.getLogger(ReasonableBot.class.getName());

	/*
	 * Basic simulation information. An agent should receive the {@link
	 * StartInfo} at the beginning of the game or during recovery.
	 */
	@SuppressWarnings("unused")
	private StartInfo startInfo;

	/**
	 * Messages received:
	 * 
	 * We keep all the {@link CampaignReport campaign reports} 
	 * delivered to the agent. We also keep the initialization 
	 * messages {@link PublisherCatalog} and
	 * {@link InitialCampaignMessage} and the most recent messages and reports
	 * {@link CampaignOpportunityMessage}, {@link CampaignReport}, and
	 * {@link AdNetworkDailyNotification}.
	 */
	protected Queue<CampaignReport> campaignReports;
	private PublisherCatalog publisherCatalog;
	private InitialCampaignMessage initialCampaignMessage;
	private AdNetworkDailyNotification adNetworkDailyNotification;

	/*
	 * The addresses of server entities to which the agent should send the daily
	 * bids data
	 */
	private String demandAgentAddress;
	protected String adxAgentAddress;
	protected String agentName;
	protected double qualityRating;

	/*
	 *  this is true if the bot is supposed to have quality rating protecting measures carried out
	 */

	protected Boolean qualityRatingProtection;

	/*
	 * we maintain a list of queries - each characterized by the web site (the
	 * publisher), the device type, the ad type, and the user market segment
	 */
	protected AdxQuery[] queries;

	/**
	 * Information regarding the latest campaign opportunity announced
	 */
	private CampaignData pendingCampaign;

	/**
	 * We maintain a collection (mapped by the campaign id) of the campaigns won
	 * by our agent.
	 */
	protected Map<Integer, CampaignData> myCampaigns;

	/*
	 * the bidBundle to be sent daily to the AdX
	 */
	protected AdxBidBundle bidBundle;

	/*
	 * The current bid level for the user classification service
	 */
	double ucsBid = 0.05;
	
	/*
	 * Establishes the max amount that the agent should spend on the ucs bid
	 */
	double ucsMaxBid = 10.0;

	/*
	 * The targeted service level for the user classification service
	 */
	double ucsTargetLevel;
	/*
	 * log of info to print out about ucs bidding
	 */
	String[] ucsLog;

	/*
	 * current day of simulation
	 */
	protected int day;

	/*
	 * current bid for campaigns; this will decrease every time 
	 * a campaign is not won and increase every time a campaign is not fulfilled
	 * 
	 */
	double persistantCampaignBid;

	protected Random randomGenerator;

	public ReasonableBot() {
		campaignReports = new LinkedList<CampaignReport>();
	}

	@Override
	protected void messageReceived(Message message) {
		try {
			Transportable content = message.getContent();

			//log.fine(message.getContent().getClass().toString());

			if (content instanceof InitialCampaignMessage) {
				handleInitialCampaignMessage((InitialCampaignMessage) content);
			} else if (content instanceof CampaignOpportunityMessage) {
				handleICampaignOpportunityMessage((CampaignOpportunityMessage) content);
			} else if (content instanceof CampaignReport) {
				handleCampaignReport((CampaignReport) content);
			} else if (content instanceof AdNetworkDailyNotification) {
				handleAdNetworkDailyNotification((AdNetworkDailyNotification) content);
			} else if (content instanceof AdxPublisherReport) {
				handleAdxPublisherReport((AdxPublisherReport) content);
			} else if (content instanceof SimulationStatus) {
				handleSimulationStatus((SimulationStatus) content);
			} else if (content instanceof PublisherCatalog) {
				handlePublisherCatalog((PublisherCatalog) content);
			} else if (content instanceof AdNetworkReport) {
				handleAdNetworkReport((AdNetworkReport) content);
			} else if (content instanceof StartInfo) {
				handleStartInfo((StartInfo) content);
			} else if (content instanceof BankStatus) {
				handleBankStatus((BankStatus) content);
			} else {
				log.info("UNKNOWN Message Received: " + content);
			}

		} catch (NullPointerException e) {
			this.log.log(Level.SEVERE,
					"Exception thrown while trying to parse message." + e);
			return;
		}
	}

	private void handleBankStatus(BankStatus content) {
		//log.info("Day " + day + " :" + content.toString());
	}

	/**
	 * Processes the start information.
	 * 
	 * @param startInfo
	 *            the start information.
	 */
	protected void handleStartInfo(StartInfo startInfo) {
		this.startInfo = startInfo;
	}

	/**
	 * Process the reported set of publishers
	 * 
	 * @param publisherCatalog
	 */
	private void handlePublisherCatalog(PublisherCatalog publisherCatalog) {
		this.publisherCatalog = publisherCatalog;
		generateAdxQuerySpace();
	}

	/**
	 * On day 0, a campaign (the "initial campaign") is allocated to each
	 * competing agent. The campaign starts on day 1. The address of the
	 * server's AdxAgent (to which bid bundles are sent) and DemandAgent (to
	 * which bids regarding campaign opportunities may be sent in subsequent
	 * days) are also reported in the initial campaign message
	 */
	private void handleInitialCampaignMessage(
			InitialCampaignMessage campaignMessage) {
		//log.info(campaignMessage.toString());

		day = 0;

		initialCampaignMessage = campaignMessage;
		demandAgentAddress = campaignMessage.getDemandAgentAddress();
		adxAgentAddress = campaignMessage.getAdxAgentAddress();

		CampaignData campaignData = new CampaignData(initialCampaignMessage);
		campaignData.setBudget(initialCampaignMessage.getReachImps() / 1000.0);

		/*
		 * The initial campaign is already allocated to our agent so we add it
		 * to our allocated-campaigns list.
		 */
		//log.info("Day " + day + ": Allocated campaign - " + campaignData);
		myCampaigns.put(initialCampaignMessage.getId(), campaignData);

	}

	/**
	 * On day n ( > 0) a campaign opportunity is announced to the competing
	 * agents. The campaign starts on day n + 2 or later and the agents may send
	 * (on day n) related bids (attempting to win the campaign). The allocation
	 * (the winner) is announced to the competing agents during day n + 1.
	 */
	private void handleICampaignOpportunityMessage(
			CampaignOpportunityMessage com) {

		day = com.getDay();

		pendingCampaign = new CampaignData(com);
		//log.info("Day " + day + ": Campaign opportunity - " + pendingCampaign);

		/*
		 * The campaign requires com.getReachImps() impressions. The competing
		 * Ad Networks bid for the total campaign Budget (that is, the ad
		 * network that offers the lowest budget gets the campaign allocated).
		 * The advertiser is willing to pay the AdNetwork at most 1$ CPM,
		 * therefore the total number of impressions may be treated as a reserve
		 * (upper bound) price for the auction.
		 */

		/*
		 * A bid of less than 0.1 per 1000 impressions is ignored: we have to bid at least 0.0001
		 */

		System.out.println("persistant campaign bid: ");
		System.out.println(String.valueOf(persistantCampaignBid));
		
		if (persistantCampaignBid < 0.1) {
			persistantCampaignBid = 0.1;
			System.out.println("bid was too low");
		}


		//the campaign auction bid
		long cmpBid = (long) (persistantCampaignBid * pendingCampaign.reachImps);


		/*
		 * Adjust ucs bid s.t. target level is achieved. Note: The bid for the
		 * user classification service is piggybacked
		 */

		if (adNetworkDailyNotification != null) {
			double ucsLevel = adNetworkDailyNotification.getServiceLevel();
			double prevUcsBid = ucsBid;
			
			//ucs logging
			String ucslog = "";
			ucslog += (String.valueOf(day) + "," + String.valueOf(ucsTargetLevel) + "," +
			          String.valueOf(ucsLevel) + "," + String.valueOf(ucsBid));
			ucsLog[day] = ucslog;
			
			if ((day % 10) == 0){
				ucsTargetLevel = (randomGenerator.nextDouble() * 0.5) + 0.5;
			}
			if (ucsBid == 0){
				ucsBid = ucsMaxBid * 0.1;
			}

			ucsBid = Math.min(prevUcsBid * (1 + ucsTargetLevel - ucsLevel), ucsMaxBid);

			/*log.info("Day " + day + ": Adjusting ucs bid: was " + prevUcsBid
					+ " level reported: " + ucsLevel + " target: "
					+ ucsTargetLevel + " adjusted: " + ucsBid); */
		} else {
			//log.info("Day " + day + ": Initial ucs bid is " + ucsBid);
		}


		log.info(agentName + " " + "DAY " + day + " bid: " + cmpBid + " qs: " + qualityRating);

		/* Note: Campaign bid is in millis */
		AdNetBidMessage bids = new AdNetBidMessage(ucsBid, pendingCampaign.id,
				cmpBid);
		sendMessage(demandAgentAddress, bids);
	}

	/**
	 * On day n ( > 0), the result of the UserClassificationService and Campaign
	 * auctions (for which the competing agents sent bids during day n -1) are
	 * reported. The reported Campaign starts in day n+1 or later and the user
	 * classification service level is applicable starting from day n+1.
	 */
	private void handleAdNetworkDailyNotification(
			AdNetworkDailyNotification notificationMessage) {

		adNetworkDailyNotification = notificationMessage;

		//log.info("Day " + day + ": Daily notification for campaign "
		//	+ adNetworkDailyNotification.getCampaignId()); 

		String campaignAllocatedTo = " allocated to "
				+ notificationMessage.getWinner();

		if ((pendingCampaign.id == adNetworkDailyNotification.getCampaignId())
				&& (notificationMessage.getCostMillis() != 0)) {

			/* add campaign to list of won campaigns */
			pendingCampaign.setBudget(notificationMessage.getCostMillis());

			myCampaigns.put(pendingCampaign.id, pendingCampaign);

			campaignAllocatedTo = " WON at cost "
					+ notificationMessage.getCostMillis();
			log.info(agentName + " " + "DAY " + day + campaignAllocatedTo);
		} 
		// Every time a campaign isn't won, the campaign bid drops by 20%
		else {
			persistantCampaignBid = persistantCampaignBid * 0.8;
			//	log.info("campaign was NOT won. new bid:" + persistantCampaignBid);
		}

		/*	log.info("Day " + day + ": " + campaignAllocatedTo
				+ ". UCS Level set to " + notificationMessage.getServiceLevel()
				+ " at price " + notificationMessage.getPrice()
				+ " Quality Score is: " + notificationMessage.getQualityScore()); */
		qualityRating = notificationMessage.getQualityScore();



	}

	/**
	 * The SimulationStatus message received on day n indicates that the
	 * calculation time is up and the agent is requested to send its bid bundle
	 * to the AdX.
	 */
	private void handleSimulationStatus(SimulationStatus simulationStatus) {
		//log.info("Day " + day + " : Simulation Status Received");
		sendBidAndAds();
		//log.info("Day " + day + " ended. Starting next day");
		++day;
	}

	/**
	 * 
	 */
	protected void sendBidAndAds() {

		bidBundle = new AdxBidBundle();
		int entrySum = 0;

		/*
		 * 
		 */
		for (CampaignData campaign : myCampaigns.values()) {

			int dayBiddingFor = day + 1;

			/* 
			 * 
			 * Makes a reasonable (hence the name) bid for each campaign:
			 * the campaign budget divided by the number of impressions
			 * that have to be bought with that budget
			 * 
			 * 
			 */


			//UPDATE: baseBid now reflects the urgency score of each campaign, i.e. impression-to-go-percentage/days-left-percentage
			double urgency = 1;
			
			if ((qualityRatingProtection) && (campaign.dayEnd - day > 0)) {
				
				urgency = (double) campaign.impsTogo() / (double) campaign.reachImps * (double) (campaign.dayEnd - campaign.dayStart) / (double) (campaign.dayEnd - day) ;
				//	log.info(campaign.impsTogo() + "/" + campaign.reachImps + "*" + (campaign.dayEnd - campaign.dayStart)+ "/" + (campaign.dayEnd - day));
				//	log.info( "DAY " + day + ": " + Arrays.toString(campaign.targetSegments.toArray()) + " urgency: " + urgency) ;
				
				if (urgency > 1.0) {
					persistantCampaignBid = persistantCampaignBid * (1.0 + 0.5 * urgency / (double) (campaign.dayEnd - campaign.dayStart));
					}
				}

			double baseBid = campaign.budget / campaign.reachImps * urgency * urgency;

			/*
			 * add bid entries w.r.t. each active campaign with remaining
			 * contracted impressions.
			 * 
			 * for now, a single entry per active campaign is added for queries
			 * of matching target segment.
			 */

			if ((dayBiddingFor >= campaign.dayStart)
					&& (dayBiddingFor <= campaign.dayEnd)
					&& (campaign.impsTogo() >= 0)) {

				int entCount = 0;
				//for each possible key (aka query, aka MarketSegment, aka UserType)...
				for (int i = 0; i < queries.length; i++) {

					/*
					 * Adjusts the bids according to video and mobile preferences
					 * of the campaign
					 */
					double rbid = baseBid;
					if (queries[i].getAdType().equals(AdType.video))
						rbid = rbid * campaign.videoCoef;
					if (queries[i].getDevice().equals(Device.mobile))
						rbid = rbid * campaign.mobileCoef;

					Set<MarketSegment> segmentsList = queries[i]
							.getMarketSegments();

					/*
					 * I think that these came from the sets of 1 called 'singleMarketSegment'
					 * so when it says for each marketSegment in segmentsList, it's really just
					 * for THE marketSegment in segmentsList
					 */

					for (MarketSegment marketSegment : segmentsList) {
						if (campaign.targetSegments.contains( marketSegment)) {
							/*
							 * among matching entries with the same campaign id,
							 * the AdX randomly chooses an entry according to
							 * the designated weight. by setting a constant
							 * weight 1, we create a uniform probability over
							 * active campaigns
							 */
							++entCount;
							bidBundle.addQuery(queries[i], rbid, new Ad(null),
									campaign.id, 1);
						} 
					}

					/*
					 * bidding on the neutral campaigns (for which ucs got no information)
					 */

					if (segmentsList.size() == 0) {
						++entCount;
						bidBundle.addQuery(queries[i], (rbid*campaign.targetSegments.size()/queries.length), new Ad(null),
								campaign.id, 1);
					}
				}





				double impressionLimit = 0.5 * campaign.impsTogo();
				double budgetLimit = 0.5 * Math.max(0, campaign.budget
						- campaign.stats.getCost());
				bidBundle.setCampaignDailyLimit(campaign.id,
						(int) impressionLimit, budgetLimit);
				//entrySum += entCount;
				//log.info("Day " + day + ": Updated " + entCount
				//			+ " Bid Bundle entries for Campaign id " + campaign.id);
			}
		}

		if (bidBundle != null) {
			//log.info("Day " + day + ": Sending BidBundle");
			sendMessage(adxAgentAddress, bidBundle);
		}
	}

	/**
	 * Campaigns performance w.r.t. each allocated campaign
	 */
	private void handleCampaignReport(CampaignReport campaignReport) {

		campaignReports.add(campaignReport);

		/*
		 * for each campaign, the accumulated statistics from day 1 up to day
		 * n-1 are reported
		 */

		/*
		 * updating stats AND logging the report
		 */
		for (CampaignReportKey campaignKey : campaignReport.keys()) {
			int cmpId = campaignKey.getCampaignId();
			CampaignStats cstats = campaignReport.getCampaignReportEntry(
					campaignKey).getCampaignStats();
			myCampaigns.get(cmpId).setStats(cstats);

			/*		log.info("Day " + day + ": Updating campaign " + cmpId + " stats: "
					+ cstats.getTargetedImps() + " tgtImps "
					+ cstats.getOtherImps() + " nonTgtImps. Cost of imps is "
					+ cstats.getCost());  */
		}


		/*
		 * updating the max ucsBid to .1 of the total campaign budget
		 */
		
		ucsMaxBid = 0;

		for (CampaignData campaign : myCampaigns.values()) { 
			if ((campaign.dayEnd - day) >= 0)
				ucsMaxBid = ucsMaxBid + campaign.budget/(campaign.dayEnd - campaign.dayStart);
		}
		


	}

	/**
	 * Users and Publishers statistics: popularity and ad type orientation
	 */
	private void handleAdxPublisherReport(AdxPublisherReport adxPublisherReport) {
		//log.info("Publishers Report: ");
		for (PublisherCatalogEntry publisherKey : adxPublisherReport.keys()) {
			AdxPublisherReportEntry entry = adxPublisherReport
					.getEntry(publisherKey);
			//	log.info(entry.toString());
		}
	}

	/**
	 * 
	 * @param AdNetworkReport
	 */
	private void handleAdNetworkReport(AdNetworkReport adnetReport) {

		//	log.info("Day "+ day + " : AdNetworkReport");

	}

	@Override
	protected void simulationSetup() {
		System.out.println("agent is being set up");
		agentName = "rB";
		qualityRatingProtection = true;
		randomGenerator = new Random();
		persistantCampaignBid = 0.2;
		day = 0;
		bidBundle = new AdxBidBundle();
		//ucsTargetLevel = .8;
		myCampaigns = new HashMap<Integer, CampaignData>();
		
		//ucs logging
		ucsTargetLevel = (randomGenerator.nextDouble() * 0.5) + 0.5;
		ucsLog = new String[60];
	}
	
	public void printArrayToFile(String[] stringArray){
		String name = "/Users/Brendan/code/adx/test_data/" + "test" + ".csv";
		File log = new File(name);
		try {
			FileWriter fw = new FileWriter(log, true);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < stringArray.length; i++){
				if (stringArray[i] != null){
					bw.write(stringArray[i]);
					bw.write('\n');
				}
			}
			bw.close();
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void simulationFinished() {
		campaignReports.clear();
		bidBundle = null;
		
		//ucs logging
		printArrayToFile(ucsLog);
	}

	/**
	 * A user visit to a publisher's web-site results in an impression
	 * opportunity (a query) that is characterized by the the publisher, the
	 * market segment the user may belongs to, the device used (mobile or
	 * desktop) and the ad type (text or video).
	 * 
	 * An array of all possible queries is generated here, based on the
	 * publisher names reported at game initialization in the publishers catalog
	 * message
	 */
	private void generateAdxQuerySpace() {
		if (publisherCatalog != null && queries == null) {
			Set<AdxQuery> querySet = new HashSet<AdxQuery>();

			/*
			 * for each web site (publisher) we generate all possible variations
			 * of device type, ad type, and user market segment
			 */
			for (PublisherCatalogEntry publisherCatalogEntry : publisherCatalog) {
				String publishersName = publisherCatalogEntry
						.getPublisherName();
				for (MarketSegment userSegment : MarketSegment.values()) {
					Set<MarketSegment> singleMarketSegment = new HashSet<MarketSegment>();
					singleMarketSegment.add(userSegment);

					querySet.add(new AdxQuery(publishersName,
							singleMarketSegment, Device.mobile, AdType.text));

					querySet.add(new AdxQuery(publishersName,
							singleMarketSegment, Device.pc, AdType.text));

					querySet.add(new AdxQuery(publishersName,
							singleMarketSegment, Device.mobile, AdType.video));

					querySet.add(new AdxQuery(publishersName,
							singleMarketSegment, Device.pc, AdType.video));

				}

				/**
				 * An empty segments set is used to indicate the "UNKNOWN" segment
				 * such queries are matched when the UCS fails to recover the user's
				 * segments.
				 */
				querySet.add(new AdxQuery(publishersName,
						new HashSet<MarketSegment>(), Device.mobile,
						AdType.video));
				querySet.add(new AdxQuery(publishersName,
						new HashSet<MarketSegment>(), Device.mobile,
						AdType.text));
				querySet.add(new AdxQuery(publishersName,
						new HashSet<MarketSegment>(), Device.pc, AdType.video));
				querySet.add(new AdxQuery(publishersName,
						new HashSet<MarketSegment>(), Device.pc, AdType.text));
			}
			queries = new AdxQuery[querySet.size()];
			querySet.toArray(queries);
		}
	}

	public class CampaignData {
		/* campaign attributes as set by server */
		Long reachImps;
		long dayStart;
		long dayEnd;
		Set<MarketSegment> targetSegments;
		double videoCoef;
		double mobileCoef;
		int id;

		/* campaign info as reported */
		CampaignStats stats;
		double budget;

		public CampaignData(InitialCampaignMessage icm) {
			reachImps = icm.getReachImps();
			dayStart = icm.getDayStart();
			dayEnd = icm.getDayEnd();
			targetSegments = icm.getTargetSegment();
			videoCoef = icm.getVideoCoef();
			mobileCoef = icm.getMobileCoef();
			id = icm.getId();

			stats = new CampaignStats(0, 0, 0);
			budget = 0.0;
		}

		public void setBudget(double d) {
			budget = d;
		}

		public CampaignData(CampaignOpportunityMessage com) {
			dayStart = com.getDayStart();
			dayEnd = com.getDayEnd();
			id = com.getId();
			reachImps = com.getReachImps();
			targetSegments = com.getTargetSegment();
			mobileCoef = com.getMobileCoef();
			videoCoef = com.getVideoCoef();
			stats = new CampaignStats(0, 0, 0);
			budget = 0.0;
		}

		@Override
		public String toString() {
			return "Campaign ID " + id + ": " + "day " + dayStart + " to "
					+ dayEnd + " " + Arrays.toString(targetSegments.toArray()) + ", reach: "
					+ reachImps + " coefs: (v=" + videoCoef + ", m="
					+ mobileCoef + ")";
		}

		int impsTogo() {
			return (int) Math.max(0, reachImps - stats.getTargetedImps());
		}

		void setStats(CampaignStats s) {
			stats.setValues(s);
		}

	}

}
