package sbw.project.cli.parser;

import java.util.ArrayList;

public class SyntaxValidator {
	
	
	public SyntaxValidator() {

	}

	public boolean createSyntaxValidator(String[] strarr) {
		testargs(strarr);
		switch (strarr[1]) {
		case "RUDDER":
			return parseOneWordCreateCommand(strarr, 2);
		case "ELEVATOR":
			return parseOneWordCreateCommand(strarr, 2);
		case "AILERON":
			return parseOneWordCreateCommand(strarr, 2);
		case "ENGINE":
			return parseOneWordCreateCommand(strarr, 2);
		case "SPLIT":
			return parseTwoWordCreateCommand(strarr, 2);
		case "FOWLER":
			return parseTwoWordCreateCommand(strarr, 2);
		case "NOSE":
			return parseTwoWordCreateCommand(strarr, 2);
		case "MAIN":
			return parseTwoWordCreateCommand(strarr, 2);
		}

		throw new RuntimeException("Invalid Syntax for create");
	}

	public boolean declareSyntaxValidator(String[] strarr) {
		testargs(strarr);
		if(strarr.length == 1) {
			teststring(strarr[0]);
			if(strarr[0].contentEquals("COMMIT")) {
				return commitSyntaxValidator(strarr);
			}
		}else {
			switch (strarr[1]) {
			case "RUDDER":
				return parseTwoWordDeclareCommand(strarr, 2);
			case "ELEVATOR":
				return parseTwoWordDeclareCommand(strarr, 2);
			case "AILERON":
				return parseTwoWordDeclareCommand(strarr, 2);
			case "FLAP":
				return parseTwoWordDeclareCommand(strarr, 2);
			case "ENGINE":
				return parseTwoWordDeclareCommand(strarr, 2);
			case "GEAR":
				return parseTwoWordDeclareCommand(strarr, 2);
			case "BUS":
				return parseOneWordDeclareCommand(strarr, 2); //only called when bus is declared
			}
			throw new RuntimeException("Invalid Syntax for declare");
		}
		throw new RuntimeException("Invalid Syntax for declare");
	}

	public boolean doSyntaxValidator(String[] strarr) {
		testargs(strarr);
		return true;
	}

	public boolean commitSyntaxValidator(String[] strarr) {
		testargs(strarr);
		return true;
	}

	public boolean clockSyntaxValidator(String[] strarr) {
		testargs(strarr);
		return true;
	}

	public boolean runSyntaxValidator(String[] strarr) {
		if(strarr.length == 2) {
			testargs(strarr);
			String normal = "";
			int x;
			for(x = 0; x < strarr.length; x++) {
					normal += strarr[x];
					normal += " ";
			}
			if(normal.lastIndexOf('"') != normal.length()-2) {
				throw new RuntimeException("Invalid Syntax for run");
			}
			String[] finalStrArr = new String [2];
			finalStrArr[0] = normal.substring(0, 4);
			finalStrArr[1] = normal.substring(5, normal.length());
			if(finalStrArr.length == 2) {
				teststring(finalStrArr[1]);
				if(finalStrArr[1].length() >= 3) {
					if(finalStrArr[1].charAt(0) == '"' && finalStrArr[1].charAt(finalStrArr[1].length()-2) == '"') {
						return true;
					}
					throw new RuntimeException("Invalid Syntax -- run requires quotation marks around the file");
				}
				throw new RuntimeException("Invalid Syntax -- file must be specified");
			}
			throw new RuntimeException("Invalid Syntax -- run needs to have only two words");
		}
		throw new RuntimeException("Invalid Syntax -- run needs to have only two words");
	}

	public boolean exitSyntaxValidator(String[] strarr) {
		testargs(strarr);
		return true;
	}

	public boolean waitSyntaxValidator(String[] strarr) {
		testargs(strarr);
		if(strarr.length == 2) {
			teststring(strarr[0]);
			if(strarr[0].contentEquals("@WAIT")){
				teststring(strarr[1]);
				if(ArgumentValidator.isValidRate(strarr[1])){
					return true;
				}
				throw new RuntimeException("Invalid Syntax -- rate is invalid");
			}
			throw new RuntimeException("Invalid Syntax -- wait is incorrectly called");
		}
		throw new RuntimeException("Invalid Syntax -- wait needs to have only two words");
	}

	private static final void testargs(String[] strarr) {
		assert (strarr != null) : "Bad strarr in SyntaxValidator " + strarr;
	}

	private static final void teststring(String str) {
		assert (str != null && !(str.isEmpty())) : "String: " + str + " is null or empty";
	}

	private boolean parseOneWordCreateCommand(String[] strarr, int initialIndex) {
		teststring(strarr[initialIndex]);
		if (ArgumentValidator.isValidIdentifier(strarr[initialIndex])) {
			teststring(strarr[initialIndex + 1]);
			if (strarr[initialIndex + 1].contentEquals("WITH")) {
				if(afterWith(strarr, initialIndex + 2)) {
					return true;
				}
			}
		}
		throw new RuntimeException("Invalid Syntax for create");
	}

	private boolean parseTwoWordCreateCommand(String[] strarr, int initialIndex) {

		teststring(strarr[initialIndex]);
		if (strarr[initialIndex] .contentEquals("FLAP") || strarr[initialIndex] .contentEquals("GEAR")) {
			teststring(strarr[initialIndex + 1]);
			if (ArgumentValidator.isValidIdentifier(strarr[initialIndex + 1])) {
				if (strarr[initialIndex + 2] .contentEquals("WITH")) {
					if(afterWith(strarr, initialIndex + 3)) {
						return true;
					}
				}
			}
		}
		throw new RuntimeException("Invalid Syntax for create");
	}
	
	private boolean parseOneWordDeclareCommand(String[] strarr, int initialIndex) {
		teststring(strarr[initialIndex]);
		if (ArgumentValidator.isValidIdentifier(strarr[initialIndex])) {
			teststring(strarr[initialIndex + 1]);
			if (strarr[initialIndex + 1].contentEquals("WITH")) {
				if(afterWithDeclarative(strarr, initialIndex + 2)) {
					return true;
				}
			}
		}
		throw new RuntimeException("Invalid Syntax for declare");
	}

	private boolean parseTwoWordDeclareCommand(String[] strarr, int initialIndex) {
		teststring(strarr[initialIndex]);
		if (strarr[initialIndex].contentEquals("CONTROLLER")) {
			teststring(strarr[initialIndex + 1]);
			if (ArgumentValidator.isValidIdentifier(strarr[initialIndex+1]) && strarr[initialIndex+2].contentEquals("WITH")) {
				if(afterWithDeclarative(strarr, initialIndex + 2)) {
					return true;
				}
			}
		}
		throw new RuntimeException("Invalid Syntax for declare");
	}

	private boolean afterWith(String[] strarr, int initialIndex) {
		int thisIsTheIndexOfWith = initialIndex;
		assert (initialIndex < strarr.length && initialIndex > 0) : "index for with is bad";

		switch (strarr[1]) {
		case "RUDDER":
			return checkRudderOrElevatorProperties(strarr, thisIsTheIndexOfWith);
		case "ELEVATOR":
			return checkRudderOrElevatorProperties(strarr, thisIsTheIndexOfWith);
		case "AILERON":
			return checkAileronProperties(strarr, thisIsTheIndexOfWith);
		case "ENGINE":
			return checkEngineProperties(strarr, thisIsTheIndexOfWith);
		case "SPLIT":
			return checkFlapProperties(strarr, thisIsTheIndexOfWith);
		case "FOWLER":
			return checkFlapProperties(strarr, thisIsTheIndexOfWith);
		case "NOSE":
			return checkGearProperties(strarr, thisIsTheIndexOfWith);
		case "MAIN":
			return checkGearProperties(strarr, thisIsTheIndexOfWith);
		default:
			throw new RuntimeException("Bad params : " + strarr + " for method afterWith in SyntaxValidator");
		}
	}
	
	private boolean afterWithDeclarative(String[] strarr, int i) {
		int thisIsTheIndexOfWith = i;
		assert (i < strarr.length && i > 0) : "index for with is bad ";
		if(!(strarr[0].contentEquals("DECLARE")))
		{
			throw new RuntimeException("Cannot invoke declarative command on command that starts with: " + strarr[0]);
		}
		switch (strarr[1]) {
		case "RUDDER":
			return checkRudderControllerProperties(strarr, thisIsTheIndexOfWith+1);
		case "ELEVATOR":
			return checkElevatorControllerProperties(strarr, thisIsTheIndexOfWith+1);
		case "AILERON":
			return checkAileronControllerProperties(strarr, thisIsTheIndexOfWith+1);
		case "FLAP":
			return checkFlapControllerProperties(strarr, thisIsTheIndexOfWith+1);
		case "ENGINE":
			return checkEngineControllerProperties(strarr, thisIsTheIndexOfWith+1);
		case "GEAR":
			return checkGearControllerProperties(strarr, thisIsTheIndexOfWith);
		case "BUS": 
			return checkBusProperties(strarr, thisIsTheIndexOfWith+1);
			
		default:
			throw new RuntimeException("Bad params : " + strarr + " for method afterWithDeclarative in SyntaxValidator");
		}
	}

	// Creational Commands
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private boolean checkGearProperties(String[] strarr, int i) {
		if(strarr.length == 9) {
			teststring(strarr[i]);
			if (strarr[i].contentEquals("SPEED")) {
				teststring(strarr[i + 1]);
				if (ArgumentValidator.isValidSpeed(strarr[i + 1])) {
					teststring(strarr[i + 2]);
					if (strarr[i + 2].contentEquals("ACCELERATION")) {
						teststring(strarr[i + 3]);
						if(ArgumentValidator.isValidAcceleration(strarr[i + 3])) {
							return true;
						}
					}
				}
			}
			throw new RuntimeException("Bad params : " + strarr + " for method checkGearProperties in SyntaxValidator");
		}
		throw new RuntimeException("Bad params for method checkGearProperties in SyntaxValidator");
	}

	private boolean checkFlapProperties(String[] strarr, int i) {
		if(strarr.length == 11) {
			teststring(strarr[i]);
			if (strarr[i].contentEquals("LIMIT")) {
				teststring(strarr[i + 1]);
				if (ArgumentValidator.isValidAngle(strarr[i + 1])) {
					teststring(strarr[i + 2]);
					if (strarr[i + 2].contentEquals("SPEED")) {
						teststring(strarr[i + 3]);
						if (ArgumentValidator.isValidSpeed(strarr[i + 3])) {
							teststring(strarr[i + 4]);
							if (strarr[i + 4].contentEquals("ACCELERATION")) {
								teststring(strarr[i + 5]);
								if(ArgumentValidator.isValidAcceleration(strarr[i + 5])) {
									return true;
								}
							}
						}
					}
				}
			}
			throw new RuntimeException("Bad params : " + strarr + " for method checkFlapProperties in SyntaxValidator");
		}
		throw new RuntimeException("Bad params for method checkFlapProperties in SyntaxValidator");
	}

	private boolean checkEngineProperties(String[] strarr, int i) {
		if(strarr.length == 8) {
			teststring(strarr[i]);
			if (strarr[i].contentEquals("SPEED")) {
				teststring(strarr[i + 1]);
				if (ArgumentValidator.isValidSpeed(strarr[i + 1])) {
					teststring(strarr[i + 2]);
					if (strarr[i + 2].contentEquals("ACCELERATION")) {
						teststring(strarr[i + 3]);
						if(ArgumentValidator.isValidAcceleration(strarr[i + 3])) {
							return true;
						}
					}
				}
			}
			throw new RuntimeException("Bad params : " + strarr + " for method checkEngineProperties in SyntaxValidator");
		}
		throw new RuntimeException("Bad params for method checkEngineProperties in SyntaxValidator");
	}

	private boolean checkAileronProperties(String[] strarr, int i) {
		if(strarr.length == 13) {
			teststring(strarr[i]);
			if (strarr[i].contentEquals("LIMIT")) {
				teststring(strarr[i + 1]);
				if (strarr[i + 1].contentEquals("UP")) {
					teststring(strarr[i + 2]);
					if (ArgumentValidator.isValidAngle(strarr[i + 2])) {
						teststring(strarr[i + 3]);
						if (strarr[i + 3].contentEquals("DOWN")) {
							teststring(strarr[i + 4]);
							if (ArgumentValidator.isValidAngle(strarr[i + 4])) {
								teststring(strarr[i + 5]);
								if (strarr[i + 5].contentEquals("SPEED")) {
									teststring(strarr[i + 6]);
									if (ArgumentValidator.isValidSpeed(strarr[i + 6]))
									{
										teststring(strarr[i + 7]);
										if (strarr[i + 7].contentEquals("ACCELERATION")) {
											teststring(strarr[i + 8]);
											if(ArgumentValidator.isValidAcceleration(strarr[i + 8])) {
												return true;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			throw new RuntimeException("Bad params : " + strarr + " for method checkAileronProperties in SyntaxValidator");
		}
		throw new RuntimeException("Bad params for method checkAileronProperties in SyntaxValidator");
	}

	private boolean checkRudderOrElevatorProperties(String[] strarr, int i) {
		if(strarr.length == 10) {
			teststring(strarr[i]);
			if(strarr[i].contentEquals("LIMIT"))
			{
				teststring(strarr[i + 1]);
				if(ArgumentValidator.isValidAngle(strarr[i + 1]))
				{
					teststring(strarr[i + 2]);
					if(strarr[i + 2].contentEquals("SPEED")){
						teststring(strarr[i + 3]);
						if(ArgumentValidator.isValidSpeed(strarr[i + 3])) 
						{		
							teststring(strarr[i + 4]);	
							if(strarr[i + 4].contentEquals("ACCELERATION"))
							{
								teststring(strarr[i + 5]);
								if(ArgumentValidator.isValidAcceleration(strarr[i + 5])) {
									return true;
								}
							}
						}
					}
				}
			}
			throw new RuntimeException("Bad params : " + strarr + " for method checkRudderOrElevatorProperties in SyntaxValidator");
		}
		throw new RuntimeException("Bad params for method checkRudderOrElevatorProperties in SyntaxValidator");
	}
	
	// Structural Commands
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private boolean checkRudderControllerProperties(String[] strarr, int i) {
		if(strarr.length == 7) {
			teststring(strarr[i]);
			if(strarr[i].contentEquals("RUDDER"))
			{
				teststring(strarr[i + 1]);
				if(ArgumentValidator.isValidIdentifier(strarr[i + 1])) {
					return true;
				}
			}
			throw new RuntimeException("Bad params : " + strarr + " for method checkRudderControllerProperties in SyntaxValidator");
		}
		throw new RuntimeException("Bad params: length must be 7 for method checkRudderControllerProperties in SyntaxValidator");
	}
	
	private boolean checkElevatorControllerProperties(String[] strarr, int i) {
		if(strarr.length == 8) {
			teststring(strarr[i]);
			if(strarr[i].contentEquals("ELEVATORS"))
			{
				teststring(strarr[i + 1]);
				if(ArgumentValidator.isValidIdentifier(strarr[i + 1]))
				{
					teststring(strarr[i + 2]);
					if(ArgumentValidator.isValidIdentifier(strarr[i + 2])) {
						return true;
					}
				}
			}
			throw new RuntimeException("Bad params : " + strarr + " for method checkElevatorControllerProperties in SyntaxValidator");
		}
		throw new RuntimeException("Bad params: length must be 8 for method checkElevatorControllerProperties in SyntaxValidator");
	}
	
	private boolean checkAileronControllerProperties(String[] strarr, int i) {
		if(strarr.length >= 9) {
			teststring(strarr[i]);
			if(strarr[i].contentEquals("AILERONS"))
			{
				int index = i+1;
				int count = 0;
				ArrayList<String> ailerons = new ArrayList<String>();
				while(index < strarr.length && !strarr[index].contentEquals("PRIMARY")) {
					teststring(strarr[index]);
					if(!ArgumentValidator.isValidIdentifier(strarr[index]))
					{
						throw new RuntimeException("Bad params for id : " + strarr[index] + " for method checkAileronControllerProperties in SyntaxValidator");
					}
					ailerons.add(strarr[index]);
					count++;
					index++;
				}
				if(index == strarr.length) {
					throw new RuntimeException("Command not long enough -- checked in method checkAileronControllerProperties in SyntaxValidator");
				}
				if(index == 6) { // command says AILERONS PRIMARY (i.e. no ids for ailerons)
					throw new RuntimeException("No aileron ids in command -- checked in method checkAileronControllerProperties in SyntaxValidator");
				}
				if(count % 2 != 0) {
					throw new RuntimeException("Odd number of ids passed into command -- must be even -- checked in method checkAileronControllerProperties in SyntaxValidator");
				}
				teststring(strarr[index]);
				if(strarr[index].contentEquals("PRIMARY"))
				{
					teststring(strarr[index+1]);
					if(ArgumentValidator.isValidIdentifier(strarr[index+1]) && isLeftWingAileron(ailerons, strarr[index+1])) {
						if(index + 2 < strarr.length) { //INDICATES THAT THERE is more to parse and a slave is next
							teststring(strarr[index+2]);
							if(strarr[index + 2].contentEquals("SLAVE")){
								int firstPossibleToken = index + 2;
								while(firstPossibleToken  < strarr.length) {
									if(strarr[firstPossibleToken].contentEquals("SLAVE"))
									{
										if(ArgumentValidator.isValidIdentifier(strarr[++firstPossibleToken]))
										{
											if(strarr[++firstPossibleToken].contentEquals("TO"))
											{
												if(ArgumentValidator.isValidIdentifier(strarr[++firstPossibleToken]))
												{
													if(strarr[++firstPossibleToken].contentEquals("BY"))
													{
														if(ArgumentValidator.isValidPercent(strarr[++firstPossibleToken]))
														{
															++firstPossibleToken;
															if(strarr[firstPossibleToken].contentEquals("PERCENT") &&
																	firstPossibleToken == strarr.length -1)
																	{
																		return true;
																	}
															else if(strarr[firstPossibleToken].contentEquals("PERCENT"))
															{
																if(firstPossibleToken + 1 <= strarr.length) {
																	{
																		firstPossibleToken ++;
																		continue;
																	}
																}
															}
															else throw new RuntimeException("Bad aileron controller syntax");
																	
														}else {
															throw new RuntimeException("Bad aileron controller syntax");
														}
													}else {
														throw new RuntimeException("Bad aileron controller syntax");
													}
												}else {
													throw new RuntimeException("Bad aileron controller syntax");
												}
													
											}else {
												throw new RuntimeException("Bad aileron controller syntax");
											}
												
										}else {
											throw new RuntimeException("Bad aileron controller syntax");
										}
									}else {
										throw new RuntimeException("Bad aileron controller syntax");
									}
								}
							}else {
								throw new RuntimeException("Bad aileron controller syntax");
							}
							
						}else {
							
							return true; //NO SLAVE PART -- ALL DONE
						}
					}
				}
			}
			throw new RuntimeException("Bad params : " + strarr + " for method checkAileronControllerProperties in SyntaxValidator");
		}
		throw new RuntimeException("Bad params: length must be greater than or equal to 9 for method checkAileronControllerProperties in SyntaxValidator");
	}
	
	//checkAileronControllerProperties helper -- also checks if aileron id was mentioned before in command
	private boolean isLeftWingAileron(ArrayList<String> aileronList, String primaryAileron) {
		if(aileronList == null || aileronList.size() <= 0) {
			throw new RuntimeException("AileronList is null or size is 0 for method isLeftWingAileron");
		}
		if(aileronList.contains(primaryAileron)) {
			int halfOfListLength = aileronList.size()/2; //already guaranteed to be even, because this is called after that check (splits it in half to check if left wing index)
			int aileronIdIndex = aileronList.indexOf(primaryAileron);
			if(aileronIdIndex < halfOfListLength) {
				return true;
			}
			throw new RuntimeException("Primary Aileron isn't on the left wing -- checked in method isLeftWingAileron");
		}
		throw new RuntimeException("Primary Aileron id is not an aileron id. Aileron id list: " + aileronList.toString());
	}
	
	private boolean checkFlapControllerProperties(String[] strarr, int i) {
		if(strarr.length >= 7) {
			teststring(strarr[i]);
			if(strarr[i].contentEquals("FLAPS"))
			{
				int index = i+1;
				int count = 0;
				ArrayList<String> flaps = new ArrayList<String>();
				while(index < strarr.length) {
					teststring(strarr[index]);
					if(!ArgumentValidator.isValidIdentifier(strarr[index]))
					{
						throw new RuntimeException("Bad params for id : " + strarr[index] + " for method checkFlapControllerProperties in SyntaxValidator");
					}
					flaps.add(strarr[index]);
					count++;
					index++;
				}
				if(count < 2) {
					throw new RuntimeException("Must have at least two flap ids to control. Number of flap ids passed in: " + count);
				}
				if(count % 2 != 0) {
					throw new RuntimeException("Odd number of ids passed into command -- must be even -- checked in method checkFlapControllerProperties in SyntaxValidator");
				}
				return true;
			}
			throw new RuntimeException("Bad params : " + strarr + " for method checkFlapControllerProperties in SyntaxValidator");
		}
		throw new RuntimeException("Bad params: length must be greater than or equal to 7 for method checkFlapControllerProperties in SyntaxValidator");
	}
	
	
	
	private boolean checkBusProperties(String[] strarr, int thisIsTheIndexOfWith)
	{
		assert strarr != null && thisIsTheIndexOfWith < strarr.length-1 : "Bad preconditions checkBusProperties";
		if(strarr.length >= 6) {
			teststring(strarr[thisIsTheIndexOfWith - 1]);
			if(strarr[thisIsTheIndexOfWith - 1].contentEquals("CONTROLLER") ||
			   strarr[thisIsTheIndexOfWith - 1].contentEquals("CONTROLLERS"))
			{
				int counter = thisIsTheIndexOfWith + 1;
				while(counter < strarr.length)
				{
					teststring(strarr[counter]);
					if(ArgumentValidator.isValidIdentifier(strarr[counter]))
					{
						counter++;
						continue;
					}
					else
					{
						throw new RuntimeException("Bad identifier checkBusProperties" + strarr[counter]);
					}
				}
				return true;
			}
			else throw new RuntimeException("Bad syntax checkBusProperties");
		}
		throw new RuntimeException("Bad params: length must be greater than or equal to 6 for method checkBusProperties in SyntaxValidator");
	}

	private boolean checkGearControllerProperties(String[] strarr, int thisIsTheIndexOfWith) {
		assert strarr != null && thisIsTheIndexOfWith < strarr.length-1 : "Bad preconditions checkGearControllerProperties";
		if(strarr.length == 11) {
			teststring(strarr[thisIsTheIndexOfWith + 1]);
			if(strarr[thisIsTheIndexOfWith + 1].contentEquals("GEAR"))
			{
				teststring(strarr[thisIsTheIndexOfWith + 2]);
				if(strarr[thisIsTheIndexOfWith + 2].contentEquals("NOSE"))
				{
					teststring(strarr[thisIsTheIndexOfWith + 3]);
					if(ArgumentValidator.isValidIdentifier(strarr[thisIsTheIndexOfWith + 3]))
					{
						teststring(strarr[thisIsTheIndexOfWith + 4]);
						if(strarr[thisIsTheIndexOfWith + 4].contentEquals("MAIN"))
						{
							teststring(strarr[thisIsTheIndexOfWith + 5]);
							if(ArgumentValidator.isValidIdentifier(strarr[thisIsTheIndexOfWith + 5]))
							{
								teststring(strarr[thisIsTheIndexOfWith + 6]);
								if(ArgumentValidator.isValidIdentifier(strarr[thisIsTheIndexOfWith + 6]))
								{
									return true;
								}
							}
						}
					}
				}
			}
			throw new RuntimeException("Bad syntax checkGearControllerProperties");
		}
		throw new RuntimeException("Bad params: length must be 11 for method checkGearControllerProperties in SyntaxValidator");
	}
	
	private boolean checkEngineControllerProperties(String[] strarr, int thisIsTheIndexOfWith) {
		assert strarr != null && thisIsTheIndexOfWith < strarr.length-1 : "Bad preconditions checkGearControllerProperties";
		teststring(strarr[thisIsTheIndexOfWith]);
		if(strarr[thisIsTheIndexOfWith].contentEquals("ENGINE") ||
				   strarr[thisIsTheIndexOfWith].contentEquals("ENGINES"))
				{
					int counter = thisIsTheIndexOfWith + 1;
					while(counter < strarr.length)
					{
						teststring(strarr[counter]);
						if(ArgumentValidator.isValidIdentifier(strarr[counter]))
						{
							counter++;
							continue;
						}
						else
						{
							throw new RuntimeException("Bad identifier checkEngineControllerProperties" + strarr[counter]);
						}
					}
					return true;
				}
		throw new RuntimeException("Bad syntax checkEngineControllerProperties");
	}
	
}
