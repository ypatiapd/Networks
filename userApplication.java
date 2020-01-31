//Ypatia Dami
//8606
package userApplication;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import ithakimodem.Modem;
import java.lang.*;

public class userApplication {
	public static void main(String[] args) {

		for (;;) { // menu epilogis
			Scanner input = new Scanner(System.in);
			System.out.println("hello! please select one of the options below");
			System.out.println("for downloading echo pachet press 1");
			System.out.println("for downloading image with no error press 2");
			System.out.println("for downloading image with error press 3");
			System.out.println("for downloading gps info press 4");
			System.out.println("for ARQ packages press 5");
			System.out.println("for exiting userApplication press 6");
			try {
				int choice = input.nextInt();
				switch (choice) {
				case 1:
					new userApplication().getEchoPackets();
					continue;
				case 2:
					new userApplication().getImage(0);
					continue;
				case 3:
					new userApplication().getImage(1);
					continue;
				case 4:
					new userApplication().getGPS();
					continue;
				case 5:
					new userApplication().ARQ();
					continue;
				case 6:
					break;

				}
			} catch (Exception e) {
				System.out.println("sorry,you didnt give a right choice");
			}
		}
	}

	public Modem createModem() {
		int data = 0;
		Modem modem;
		modem = new Modem();
		modem.setSpeed(82000);
		modem.setTimeout(20000);
		modem.write("atd2310ithaki\r".getBytes());
		for (;;) {
			try {
				data = modem.read();
				if (data == -1)
					break;
				System.out.print((char) data);
			} catch (Exception x) {
				break;
			}
		}
		return modem;
	}

	public void getEchoPackets() {
		String code;
		int preData = 0, afterData = 0;// metavlites apothikefsis dedomenwn
		int numOfPackets = 0;// arithmos paketwn pou elifthisan
		double startTime = 0, endTime = 0, totalTime = 0, startDL = 0, endDL = 0, avgTime = 0;

		Scanner input = new Scanner(System.in);
		ArrayList<String> DLinfo = new ArrayList<String>();// apothikefsi pliroforiwn lipsis
		System.out.println("please instert the code for recieving packet in the form:XXXX");
		code = input.nextLine();
		Modem modem;
		modem = createModem();
		startDL = System.nanoTime();
		while (endDL < 4 * 60 * 1000) {
			modem.write(("E" + code + "\r").getBytes());
			numOfPackets++;
			startTime = System.nanoTime();
			for (;;) {
				try {
					preData = afterData;
					afterData = modem.read();
					System.out.print((char) afterData);
					if (((char) preData == 'O') && ((char) afterData == 'P')) {
						endTime = (System.nanoTime() - startTime) / 1000000;
						break;
					}
				} catch (Exception e) {
					System.out.println("packet download wasn't successfull");
				}

			}
			DLinfo.add(String.valueOf(endTime));
			totalTime += endTime;
			endDL = (System.nanoTime() - startDL) / 1000000;
			System.out.println(" ");
		}
		avgTime = totalTime / numOfPackets;
		DLinfo.add("Total time of download:" + String.valueOf(totalTime) + "milisec");
		System.out.println("Total time of download:" + String.valueOf(totalTime) + "milisec");
		DLinfo.add("Total time of Comunication with server:" + String.valueOf(endDL) + "milisec");
		System.out.println("Total time of Comunication with server:" + String.valueOf(endDL) + "milisec");
		DLinfo.add("Number of packets downloaded: " + String.valueOf(numOfPackets));
		System.out.println("Number of packets downloaded: " + String.valueOf(numOfPackets));
		DLinfo.add("Average time for packet download : " + String.valueOf(avgTime) + "milisec");
		System.out.println("Average time for packet download : " + String.valueOf(avgTime) + "milisec");

		BufferedWriter BW = null;
		try {
			File echofile = new File("C:\\Users\\ypatiapd\\Desktop\\δικτυα\\Echo" + code + ".txt");
			BW = new BufferedWriter(
					new FileWriter(("C:\\Users\\ypatiapd\\Desktop\\δικτυα\\Echo" + code + ".txt"), true));
			if (!echofile.exists()) {
				echofile.createNewFile();
			}
			for (int i = 0; i < DLinfo.size(); i++) {
				BW.write(String.valueOf(DLinfo.get(i)));
				BW.newLine();
			}
			BW.newLine();
		} catch (Exception e) {
			System.out.println("couldn't write a echofile");
		} finally {
			try {
				if (BW != null)
					BW.close();
			} catch (Exception e) {
				System.out.println("couldn't close the BufferedWriter" + e);
			}
		}

	}

	public void getImage(int er) {
		ArrayList<Integer> Image = new ArrayList<Integer>(); // apothikevetai i eikona san ascii
		String type = " ";
		String code = " ";
		String fileName = " ";
		int preData = 0, afterData = 0;
		Scanner input = new Scanner(System.in);
		if (er == 0)
			type = "M";
		else
			type = "G";
		System.out.println("please insert the code for downloading image in form XXXX");
		code = input.nextLine();
		Modem modem;
		modem = createModem();
		modem.write((type + code + "\r").getBytes());
		for (;;) {
			try {
				preData = afterData;
				afterData = modem.read();
				System.out.println(afterData);
				Image.add(afterData);
				if ((preData == 255) && (afterData == 217))
					break;

			} catch (Exception e) {
				System.out.println("couldnt download image");
			}
		}
		if (er == 0)
			fileName = ("C:\\Users\\ypatiapd\\Desktop\\δικτυα\\imageNoError" + code + ".jpeg");
		else
			fileName = ("C:\\Users\\ypatiapd\\Desktop\\δικτυα\\imageError" + code + ".jpeg");
		try {
			FileOutputStream imageFile = new FileOutputStream(fileName);
			for (int i = 0; i < Image.size(); i++) {
				imageFile.write(Image.get(i));
			}
			imageFile.close();
		} catch (Exception e) {
			System.out.println(" couldnt create file");
		}
		System.out.println(" ");
		System.out.println("image download was successfull");
		modem.close();
	}

	public void getGPS() {
		String Tpar = new String();// apothikefsi Tparametrou se morfi string
		int[] gpsUnit = new int[80];// apothikefsi dedomenwn tis kathe enotitas se int pinaka
		int[][] latlong = new int[2][10];// apomonosi g.mikous kai g.platous se disdiastato pinaka
		String[] TparApparts = new String[6];// pinakas pou apothikevei ta stoixeia tis T parametrou prin enopoihthoun
												// se ena string
		ArrayList<String> Tparameters = new ArrayList<String>();// arraylist pou apothikevei oles tis Tparametrous
		ArrayList<Integer> Image = new ArrayList<Integer>();// arraylist pou apothikevei ta dedomena tis eikonas gps
		float[] latInfo = new float[6];// stoixeia g.platous
		float[] longInfo = new float[6];// stoixeia g.mikous
		float[] latDigInfo = new float[6];// stoixeia gwnias g.platous
		float[] longDigInfo = new float[6];// stoixeia gwnias g.mikous
		String fileName = " ";
		String code = " ";
		int pointer = 0;// metritis gia ton pinaka gpsUnit
		int preData = 0, afterData = 0;
		Scanner input = new Scanner(System.in);
		System.out.println("please insert the code for downloading gps units");
		code = input.nextLine();
		Modem modem;
		modem = createModem();
		modem.write(("P" + code + "R=1000199" + "\r").getBytes());
		for (;;) {
			try {

				preData = afterData;
				afterData = modem.read();
				gpsUnit[pointer] = afterData;

				System.out.print((char) gpsUnit[pointer]);
				pointer++;
				if (((char) preData == 'N') && ((char) afterData == 'G'))// ews ayto to simeio tis enotitas ta dedomena
																			// den mas aforoun
					continue;
				if (((char) preData == 'O') && ((char) afterData == 'P'))// sto telos i kathe enotita exei STOP
					break;

				if ((preData == 13) && (afterData == 10)) {
					for (int i = 0; i < 10; i++) {
						latlong[0][i] = gpsUnit[i + 18];// apo ayta ta simeia tis gpsUnit apomonwnoume g.platos kai
														// g.mikos kai ta apothikevoume ston pinaka latlong
						latlong[1][i] = gpsUnit[i + 30];
						// System.out.print((char)latlong[0][i]);

					}
					// afairoume 48 apo tin kathe timi tou pinaka gia na paroume tin arithmitiki
					// timi kai
					// analoga me ti thesi tou psifeiou diairoume me katallili dynami tou 10
					// etsi wste me tin prosthesi aytwn se mia metavliti na prokypsei o arithmos tou
					// mikous kai tou platous antistoixa
					latInfo[0] = (float) (latlong[0][2] - 48) / 10;
					latInfo[1] = (float) (latlong[0][3] - 48) / 100;
					latInfo[2] = (float) (latlong[0][5] - 48) / 1000;
					latInfo[3] = (float) (latlong[0][6] - 48) / 10000;
					latInfo[4] = (float) (latlong[0][7] - 48) / 100000;
					latInfo[5] = (float) (latlong[0][8] - 48) / 1000000;

					longInfo[0] = (float) (latlong[1][3] - 48) / 10;
					longInfo[1] = (float) (latlong[1][4] - 48) / 100;
					longInfo[2] = (float) (latlong[1][6] - 48) / 1000;
					longInfo[3] = (float) (latlong[1][7] - 48) / 10000;
					longInfo[4] = (float) (latlong[1][8] - 48) / 100000;
					longInfo[5] = (float) (latlong[1][9] - 48) / 1000000;

					// paromoia diadikasia gia tis gwnies

					latDigInfo[0] = (float) (latlong[0][0] - 48) * 10;
					latDigInfo[1] = (float) (latlong[0][1] - 48);
					longDigInfo[0] = (float) (latlong[1][1] - 48) * 10;
					longDigInfo[1] = (float) (latlong[1][2] - 48);

					// stis metavlites aytes apothikevontai oi arithmitikes times pou prokyptoun apo
					// tin
					// parapanw diadikasia kai tha symptyxthoun gia na dimiourgisoun tin T parametro
					float lattitudeMins = 0, longtitudeMins = 0, lattitudeSec = 0, longtitudeSec = 0;
					float latDigrees = 0, longDigrees = 0;
					for (int i = 0; i < 6; i++) {
						lattitudeMins += latInfo[i];
						longtitudeMins += longInfo[i];

					}
					for (int j = 0; j < 2; j++) {
						latDigrees += latDigInfo[j];
						longDigrees += longDigInfo[j];
					}
					System.out.print(latDigrees + "latDig");
					System.out.print(longDigrees + "longDig");
					lattitudeMins *= 100.0;
					longtitudeMins *= 100.0;
					longtitudeSec = (longtitudeMins % 1) * 60;
					lattitudeSec = (lattitudeMins % 1) * 60;
					System.out.print(lattitudeMins + "latMins");
					System.out.print(longtitudeMins + "longMins");
					System.out.print(lattitudeSec + "latsec");
					System.out.print(longtitudeSec + "longSec");

					// apothikefsi ston pinaka TparApparts twn timwn se string morfi

					TparApparts[0] = String.valueOf(longDigrees);
					TparApparts[1] = String.valueOf(longtitudeMins);
					TparApparts[2] = String.valueOf(longtitudeSec);
					TparApparts[3] = String.valueOf(latDigrees);
					TparApparts[4] = String.valueOf(lattitudeMins);
					TparApparts[5] = String.valueOf(lattitudeSec);

					for (int j = 0; j < 6; j++) {
						TparApparts[j] = TparApparts[j].substring(0, 2); // apomonwsi twn 2 prwtwn psifeiwn apo kathe
																			// timi
					}
					String Tparameter = new String();
					Tparameter = (TparApparts[0] + TparApparts[1] + TparApparts[2] + TparApparts[3] + TparApparts[4]
							+ TparApparts[5]); // synenwsi twn string timwn tou pinaka TparApparts se ena string
												// Tparameter

					pointer = 0;

					Tparameters.add(Tparameter);// apothikefsi sto arraylist twn T parametrwn

				}
			} catch (Exception e) {
				System.out.println("couldnt download gps info");
			}

		}
		modem.close();

		System.out.println("please insert the code for downloading gps image");
		code = input.nextLine();
		modem = createModem();
		modem.write(("P" + code + "T=" + Tparameters.get(1) + "T=" + Tparameters.get(30) + "T=" + Tparameters.get(60)
				+ "T=" + Tparameters.get(90) + "\r").getBytes());
		for (;;) {
			try {
				preData = afterData;
				afterData = modem.read();

				System.out.println(afterData);
				Image.add(afterData);
				if ((preData == 255) && (afterData == 217)) {
					break;
				}

			} catch (Exception e) {
				System.out.println("couldnt download gps image");
			}
		}

		fileName = ("C:\\Users\\ypatiapd\\Desktop\\δικτυα\\imageGps.jpg");

		try {
			FileOutputStream imageFile = new FileOutputStream(fileName);
			for (int j = 0; j < Image.size(); j++) {
				imageFile.write(Image.get(j));
			}
			imageFile.close();
		} catch (Exception e) {
			System.out.println(" couldnt create file");
		}
		System.out.println(" ");
		System.out.println("image download was successfull");

		modem.close();
	}

	void ARQ() {
		String Acode = " ", Ncode = " ";
		int nack = 0;// an ginei 1 dilwnei oti egine to prwto error request opote i metavliti
						// numOfErrors ananewnetai mono otan bei gia prwti fora ston deytero vroxo while
						// i roi
		int xor = 0;// apothikefsi tou xor tis symvoloseiras
		int fcs = 0;// apothikefsi tou fcs
		float ber = 0;// apothikefsi tou bit error rate
		int preData = 0, afterData = 0, numOfPackets = 0;
		int numOfErrors = 0;// arithmos paketwn pou stalthikan me error(xwris tis epanapostoles)
		int counter = 0;// metritis gia ton pinaka numOfTries
		int[] numOfTries = new int[10];// kathe thesi tou pinaka dilwnei tis fores epanapostolis pou xreiazontai gia
										// kathe paketo
		for (int i = 0; i < 10; i++) {
			numOfTries[i] = 0;
		}
		double startDL = 0, endDL = 0, startTime = 0, endTime = 0, totalTime = 0;
		Scanner input = new Scanner(System.in);
		ArrayList<String> DLinfo = new ArrayList<String>();
		System.out.println("please insert ack code");
		Acode = input.nextLine();
		System.out.println("please insert nack code");
		Ncode = input.nextLine();
		Modem modem;
		modem = createModem();
		startDL = System.nanoTime();
		while (endDL < 4 * 60 * 1000) {

			modem.write(("Q" + Acode + "\r").getBytes());
			numOfPackets++;
			startTime = System.nanoTime();
			for (;;) {
				try {

					preData = afterData;
					afterData = modem.read();
					System.out.print((char) afterData);
					if (preData == '<') {
						while (afterData != '>') {
							xor = xor ^ (char) afterData;
							afterData = modem.read();// apothikevoume tin symvoloseira kai kanoume to diadoxiko xor
							System.out.print((char) afterData);

						}
						System.out.print("xor=" + xor);
					}
					if ((preData == '>') && (afterData == ' ')) {// apothikevoume to fcs
						afterData = modem.read();
						fcs = Character.getNumericValue((char) afterData) * 100;
						afterData = modem.read();
						fcs = fcs + Character.getNumericValue((char) afterData) * 10;
						afterData = modem.read();
						fcs = fcs + Character.getNumericValue((char) afterData);
						System.out.print("fcs=" + fcs);

					}
					if (((char) preData == 'O') && ((char) afterData == 'P')) {
						System.out.println(" ");
						break;
					}

				} catch (Exception e) {
					System.out.println("couldnt download packet");
				}

			}
			counter = 0;
			nack = 0;
			while (xor != fcs) {// an den einai isa mpainoume se allo vroxo while opou zitame epanapostoli mexri
								// to
								// xor na isoutai me to fcs kai na epistrepsoume ston arxiko vroxo
				if (nack == 0)
					numOfErrors++;
				nack = 1;// to numOfErrors ananewnetai mono tin prwti fora pou bainoume sto vroxo
				xor = 0;
				fcs = 0;
				modem.write(("R" + Ncode + "\r").getBytes());
				counter++;// o counter ayksanetai analoga tis epanapostoles
				for (;;) {
					try {

						preData = afterData;
						afterData = modem.read();
						System.out.print((char) afterData);
						if (preData == '<') {
							while (afterData != '>') {
								xor = xor ^ (char) afterData;
								afterData = modem.read();
								System.out.print((char) afterData);

							}
							System.out.print("xor=" + xor);
						}
						if ((preData == '>') && (afterData == ' ')) {
							afterData = modem.read();
							fcs = Character.getNumericValue((char) afterData) * 100;
							afterData = modem.read();
							fcs = fcs + Character.getNumericValue((char) afterData) * 10;
							afterData = modem.read();
							fcs = fcs + Character.getNumericValue((char) afterData);
							System.out.print("fcs=" + fcs + "  err");

						}
						if (((char) preData == 'O') && ((char) afterData == 'P')) {
							System.out.println(" ");
							break;
						}
					} catch (Exception e) {
						System.out.println("couldnt download packet");
					}
				}

			}
			numOfTries[counter] = numOfTries[counter] + 1;// prosthetoume ena sti thesi tou pinaka isi me ton
															// counter,diladi ton arithmo epanapostolwn
			endTime = (System.nanoTime() - startTime) / 1000000;
			totalTime += endTime;
			// System.out.print("Time to recieve packet was"+ endTime +"milisec");
			DLinfo.add(String.valueOf(endTime));
			endDL = (System.nanoTime() - startDL) / 1000000;

		}
		ber = (float) numOfErrors / (numOfPackets * 59);// bit error rate
		System.out.print("\n\nNumber of packets recieved was " + numOfPackets + "\n");
		System.out.print("Total time of communication : " + (totalTime / 60) / 1000 + " minutes\n");
		System.out.print(
				"Average time to recieve one packet : " + ((totalTime / 60) / numOfPackets) / 1000 + "minutes \n");
		System.out.print("The number of errors in " + numOfPackets + " was " + numOfErrors + " \n");
		System.out.print("Total time of downloading : " + (endDL / 60) / 1000 + " minutes \n");
		System.out.print("Bit error rate : " + ber + "\n");

		DLinfo.add("\n\nNumber of packets recieved was " + numOfPackets + "\n");
		DLinfo.add("Total time of communication : " + (totalTime / 60) / 1000 + " minutes\n");
		DLinfo.add("Average time to recieve one packet : " + ((totalTime) / numOfPackets) / 1000 + "minutes \n");
		DLinfo.add("The number of errors in " + numOfPackets + "packets  was " + numOfErrors + " \n");
		DLinfo.add("Total time of downloading " + (endDL / 60) / 1000 + "minutes \n");
		DLinfo.add("Bit error rate : " + ber);
		DLinfo.add("\n");

		for (int i = 0; i < 10; i++) {
			System.out.print(numOfTries[i] + "packets needed " + i + "requests\n");
			DLinfo.add(numOfTries[i] + "packets needed " + i + "requests\n");
		}
		BufferedWriter BW = null;
		try {
			File ARQfile = new File("C:\\Users\\ypatiapd\\Desktop\\δικτυα\\ARQ.txt");
			BW = new BufferedWriter(new FileWriter(("C:\\Users\\ypatiapd\\Desktop\\δικτυα\\ARQ.txt"), true));
			if (!ARQfile.exists()) {
				ARQfile.createNewFile();
			}
			for (int i = 0; i < DLinfo.size(); i++) {
				BW.write(String.valueOf(DLinfo.get(i)));
				BW.newLine();
			}
			BW.newLine();
		} catch (Exception e) {
			System.out.println("couldn't write an ARQ file");
		} finally {
			try {
				if (BW != null)
					BW.close();
			} catch (Exception e) {
				System.out.println("couldn't close the BufferedWriter" + e);
			}
		}

		modem.close();
	}

}
