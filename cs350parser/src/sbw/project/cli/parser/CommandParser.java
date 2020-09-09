package sbw.project.cli.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import sbw.architecture.datatype.*;
import sbw.project.cli.CommandLineInterface;
import sbw.project.cli.action.*;
import sbw.project.cli.action.command.behavioral.CommandDoDeploySpeedBrake;
import sbw.project.cli.action.command.behavioral.CommandDoHalt;
import sbw.project.cli.action.command.behavioral.CommandDoSelectGear;
import sbw.project.cli.action.command.behavioral.*;
import sbw.project.cli.action.command.misc.CommandDoClockUpdate;
import sbw.project.cli.action.command.misc.CommandDoExit;
import sbw.project.cli.action.command.misc.CommandDoRunCommandFile;
import sbw.project.cli.action.command.misc.CommandDoSetClockRate;
import sbw.project.cli.action.command.misc.CommandDoSetClockRunning;
import sbw.project.cli.action.command.misc.CommandDoShowClock;
import sbw.project.cli.action.command.misc.CommandDoWait;

public class CommandParser {

	public static void main(String[] args) {
		CommandLineInterface cli = new CommandLineInterface();
		cli.execute();
	}

	private ActionSet actionSet;
	private String command;
	private Stack<String> commandStack;
	private ActionCreational actionCreational;
	private ActionStructural actionStructural;
	private ActionBehavioral actionBehavioral;
	private ActionMiscellaneous actionMiscellaneous;
	private SyntaxValidator syntaxValidator;
	private final ArrayList<String> dictionaryOfCaseInsensitiveWords;
	private final String[] dictionary = new String[] { "CREATE", "RUDDER", "WITH", "LIMIT", "SPEED", "ACCELERATION",
			"ELEVATOR", "AILERON", "UP", "DOWN", "SPLIT", "FLAP", "FOWLER", "ENGINE", "NOSE", "GEAR", "MAIN", "DECLARE",
			"CONTROLLER", "ELEVATORS", "AILERONS", "PRIMARY", "SLAVE", "TO", "BY", "PERCENT", "FLAPS", "ENGINE",
			"ENGINES", "BUS", "CONTROLLERS", "COMMIT", "DEFLECT", "LEFT", "RIGHT", "UP", "DOWN", "DO", "BRAKE", "ON",
			"OFF", "SET", "POWER", "HALT", "@CLOCK", "PAUSE", "RESUME", "UPDATE", "@RUN", "@EXIT", "@WAIT" };

	public CommandParser(ActionSet as, String command) {
		this.actionSet = as;
		this.command = command;
		this.commandStack = new Stack<>();
		this.actionCreational = this.actionSet.getActionCreational();
		this.actionBehavioral = this.actionSet.getActionBehavioral();
		this.actionStructural = this.actionSet.getActionStructural();
		this.actionMiscellaneous = this.actionSet.getActionMiscellaneous();
		this.syntaxValidator = new SyntaxValidator();
		this.takeOutComments();
		this.seperateMultipleCommandsBySemicolonAndPushToStack();
		this.dictionaryOfCaseInsensitiveWords = new ArrayList<>();
		this.dictionaryOfCaseInsensitiveWords.addAll(Arrays.asList(dictionary));
	}

	public void parse() throws RuntimeException {
		String currentCommand = commandStack.pop();
		if(!currentCommand.contentEquals("nothing - from seperateMultipleCommandsBySemicolonAndPushToStack()")) {
			while (currentCommand != null && !currentCommand.isEmpty()) {
				String[] strarr = tokenizeSingleCommand(currentCommand, "\\s+");
				switch (strarr[0]) {
				case "CREATE":
					createBuilder(strarr);
					break;
				case "DECLARE":
					declareBuilder(strarr);
					break;
				case "DO":
					doBuilder(strarr);
					break;
				case "HALT":
					doBuilder(strarr);
					break;
				case "COMMIT":
					declareCommit();
					break;
				case "@CLOCK":
					miscClock(strarr);
					break;
				case "@RUN":
					miscRun(strarr);
					break;
				case "@EXIT":
					miscExit(strarr);
					break;
				case "@WAIT":
					miscWait(strarr);
					break;
				}
				if ((!commandStack.isEmpty()))
					currentCommand = commandStack.pop();
				else
					break;
			}
		}
	}

	private void seperateMultipleCommandsBySemicolonAndPushToStack() throws RuntimeException {
		// Stack will only have one command on it if there are no semicolons
		if (this.command == null) {
			throw new RuntimeException("Bad params str method getAction for param: " + this.command);
		}
		if(this.command.isEmpty()) {
			this.commandStack.push("nothing - from seperateMultipleCommandsBySemicolonAndPushToStack()");
		}else {
			String[] seperateCommandsIfAny = this.command.split(" ; ");
			for(int x = seperateCommandsIfAny.length - 1; x >= 0; x--){
				String s = seperateCommandsIfAny[x];
				if (s == null || s.isEmpty())
				{
					throw new RuntimeException("Parsing error for line: " + this.command);
				}
				this.commandStack.push(s);
			}
		}
	}
	
	private void takeOutComments() {
		if (this.command == null || this.command.isEmpty()) {
			throw new RuntimeException("Bad params str method getAction for param: " + this.command);
		}
		if(this.command.substring(0, 2).contentEquals("//")) {
			this.command = "";
		}else {
			String[] takeOutCommentsIfAny = this.command.split("//");
			for (String s : takeOutCommentsIfAny) {
				if (s == null || s.isEmpty()){
					throw new RuntimeException("Parsing error for line: " + this.command);
				}
			}
			this.command = takeOutCommentsIfAny[0];
		}
	}

	private String[] tokenizeSingleCommand(String str, String delimiter) throws RuntimeException {
		String[] strarr = str.split(delimiter);
		int x = 0;
		for (String s : strarr) {
			if (this.dictionaryOfCaseInsensitiveWords.contains(s.toUpperCase())) {
				strarr[x] = s.toUpperCase();
			}
			x++;
		}
		if (tokenizedCommandIsSyntacticallyCorrect(strarr)) {
			return strarr;
		} else {
			throw new RuntimeException("Parsing error for line: " + str);
		}
	}

	private boolean tokenizedCommandIsSyntacticallyCorrect(String[] strarr) {
		switch (strarr[0].trim()) {
		case "CREATE":
			return this.syntaxValidator.createSyntaxValidator(strarr);
		case "DECLARE":
			return this.syntaxValidator.declareSyntaxValidator(strarr);
		case "DO":
			return this.syntaxValidator.doSyntaxValidator(strarr);
		case "HALT":
			return this.syntaxValidator.doSyntaxValidator(strarr);
		case "COMMIT":
			return this.syntaxValidator.commitSyntaxValidator(strarr);
		case "@CLOCK":
			return this.syntaxValidator.clockSyntaxValidator(strarr);
		case "@RUN":
			return this.syntaxValidator.runSyntaxValidator(strarr);
		case "@EXIT":
			return this.syntaxValidator.exitSyntaxValidator(strarr);
		case "@WAIT":
			return this.syntaxValidator.waitSyntaxValidator(strarr);
		default:
			throw new RuntimeException("Parsing error in tokenizedCommandIsSyntacticallyCorrect method");
		}
	}

	/////////////////////////////////////////////////////////////////////////////
	// Creational Commands

	private void createBuilder(String[] strarr) {
		switch (strarr[1]) {
		case "RUDDER":
			createRudder(strarr);
			break;
		case "ELEVATOR":
			createElevator(strarr);
			break;
		case "AILERON":
			createAileron(strarr);
			break;
		case "ENGINE":
			createEngine(strarr);
			break;
		case "SPLIT":
			createSplitFlap(strarr);
			break;
		case "FOWLER":
			createFowlerFlap(strarr);
			break;
		case "NOSE":
			createNoseGear(strarr);
			break;
		case "MAIN":
			createMainGear(strarr);
			break;
		default:
			throw new RuntimeException("Parsing error for createBuilder method");
		}
	}

	private void createRudder(String[] command) {
		if (syntaxValidator.createSyntaxValidator(command)) {
			actionCreational.doCreateRudder(new Identifier(command[2]), new Angle(Double.parseDouble(command[5])),
					new Speed(Double.parseDouble(command[7])), new Acceleration(Double.parseDouble(command[9])));
		}
	}

	private void createElevator(String[] command) {
		if (syntaxValidator.createSyntaxValidator(command)) {

			actionCreational.doCreateElevator(new Identifier(command[2]), new Angle(Double.parseDouble(command[5])),
					new Speed(Double.parseDouble(command[7])), new Acceleration(Double.parseDouble(command[9])));
		}
	}

	private void createAileron(String[] command) {
		if (syntaxValidator.createSyntaxValidator(command)) {
			actionCreational.doCreateAileron(new Identifier(command[2]), new Angle(Double.parseDouble(command[6])),
					new Angle(Double.parseDouble(command[8])), new Speed(Double.parseDouble(command[10])),
					new Acceleration(Double.parseDouble(command[12])));
		}
	}

	private void createEngine(String[] validStrArray) {
		if (syntaxValidator.createSyntaxValidator(validStrArray)) {
			actionCreational.doCreateEngine(new Identifier(validStrArray[2]),
					new Speed(Double.parseDouble(validStrArray[5])),
					new Acceleration(Double.parseDouble(validStrArray[7])));
		}
	}

	private void createNoseGear(String[] validStrArray) {
		if (syntaxValidator.createSyntaxValidator(validStrArray)) {
			actionCreational.doCreateGearNose(new Identifier(validStrArray[3]),
					new Speed(Double.parseDouble(validStrArray[6])),
					new Acceleration(Double.parseDouble(validStrArray[8])));
		}
	}

	private void createMainGear(String[] validStrArray) {
		if (syntaxValidator.createSyntaxValidator(validStrArray)) {
			actionCreational.doCreateGearMain(new Identifier(validStrArray[3]),
					new Speed(Double.parseDouble(validStrArray[6])),
					new Acceleration(Double.parseDouble(validStrArray[8])));
		}
	}

	private void createFowlerFlap(String[] strarr) {
		createSplitFlap(strarr);
	}

	private void createSplitFlap(String[] strarr) {
		if (this.syntaxValidator.createSyntaxValidator(strarr)) {
			actionCreational.doCreateFlap(new Identifier(strarr[3]), (strarr[1].contentEquals("FOWLER")),
					new Angle(Double.parseDouble(strarr[6])), new Speed(Double.parseDouble(strarr[8])),
					new Acceleration(Double.parseDouble(strarr[10])));
		}
	}

	/////////////////////////////////////////////////////////////////////////////
	// STRUCTURAL COMMANDS
	private void declareBuilder(String[] strarr) {
			switch (strarr[1]) {
			case "RUDDER":
				declareRudderController(strarr);
				break;
			case "ELEVATOR":
				declareElevatorController(strarr);
				break;
			case "AILERON":
				declareAileronController(strarr);
				break;
			case "FLAP":
				declareFlapController(strarr);
				break;
			case "ENGINE":
				declareEngineController(strarr);
				break;
			case "GEAR":
				declareGearController(strarr);
				break;
			case "BUS":
				declareBusController(strarr);
				break;
			default:
				throw new RuntimeException("Parsing error for declareBuilder method");
			}
	}

	private void declareBusController(String[] strarr) {
		if (this.syntaxValidator.declareSyntaxValidator(strarr)) {
			Identifier busID = new Identifier(strarr[2]);
			List<Identifier> listOfControllerIDs = new ArrayList<>();
			for (int i = 5; i < strarr.length; i++) {
				listOfControllerIDs.add(new Identifier(strarr[i]));
			}
			actionStructural.doDeclareBus(busID, listOfControllerIDs);

		} else {
			throw new RuntimeException("Parsing error for declareRudderController method");
		}
	}

	private void declareGearController(String[] strarr) {
		if (this.syntaxValidator.declareSyntaxValidator(strarr)) {
			Identifier controllerid = new Identifier(strarr[3]);
			Identifier noseid = new Identifier(strarr[7]);
			Identifier mainid1 = new Identifier(strarr[9]);
			Identifier mainid2 = new Identifier(strarr[10]);
			actionStructural.doDeclareGearController(controllerid, noseid, mainid1, mainid2);
		} else {
			throw new RuntimeException("Parsing error for declareRudderController method");
		}
	}

	private void declareEngineController(String[] strarr) {
		if (this.syntaxValidator.declareSyntaxValidator(strarr)) {
			Identifier controllerid = new Identifier(strarr[3]);
			List<Identifier> listOfEngineIDs = new ArrayList<>();
			for (int i = 6; i < strarr.length; i++) {
				listOfEngineIDs.add(new Identifier(strarr[i]));
			}
			actionStructural.doDeclareEngineController(controllerid, listOfEngineIDs);

		} else {
			throw new RuntimeException("Parsing error for declareRudderController method");
		}
	}

	private void declareFlapController(String[] strarr) {
		if (this.syntaxValidator.declareSyntaxValidator(strarr)) {
			Identifier controllerid = new Identifier(strarr[3]);
			List<Identifier> listOfFlapIDs = new ArrayList<>();
			for (int i = 6; i < strarr.length; i++) {
				listOfFlapIDs.add(new Identifier(strarr[i]));
			}
			actionStructural.doDeclareFlapController(controllerid, listOfFlapIDs);
		} else {
			throw new RuntimeException("Parsing error for declareRudderController method");
		}
	}

	private void declareAileronController(String[] strarr) {
		Identifier idCont = new Identifier(strarr[3]);
		int index = 6;
		ArrayList<Identifier> ailerons = new ArrayList<>();
		while(!strarr[index].contentEquals("PRIMARY")) {
			ailerons.add(new Identifier(strarr[index]));
			index++;
		}
		
		Identifier idPrimary = new Identifier(strarr[index+1]);
		ArrayList<AileronSlaveMix> slaveMixes = new ArrayList<>();
		if(index + 2 < strarr.length) { //INDICATES THAT THERE is more to parse and a slave is next
			if(strarr[index + 2].contentEquals("SLAVE")){
				int firstPossibleToken = index + 2;
				while(firstPossibleToken  < strarr.length) {
					if(strarr[firstPossibleToken].contentEquals("SLAVE"))
					{
						firstPossibleToken++;
						Identifier idSlave = new Identifier(strarr[firstPossibleToken]);
							if(strarr[++firstPossibleToken].contentEquals("TO"))
							{
								Identifier idMaster = new Identifier(strarr[++firstPossibleToken]);
									if(strarr[++firstPossibleToken].contentEquals("BY"))
									{
										Percent percent = new Percent(Double.parseDouble(strarr[++firstPossibleToken]));
											++firstPossibleToken;
											if(strarr[firstPossibleToken].contentEquals("PERCENT"))
											{
												slaveMixes.add(new AileronSlaveMix(idMaster, idSlave, percent));
												if(firstPossibleToken + 1 <= strarr.length) {
													{
														firstPossibleToken ++;
														continue;
													}
												}
													
											}
									}
								}
									
							}
								
						}
					}
			}
		actionStructural.doDeclareAileronController(idCont, ailerons, idPrimary, slaveMixes);
	}

	private void declareElevatorController(String[] strarr) {
		if (this.syntaxValidator.declareSyntaxValidator(strarr)) {
			Identifier controllerid = new Identifier(strarr[3]);
			Identifier firstelevatorid = new Identifier(strarr[6]);
			Identifier secondelevatorid = new Identifier(strarr[7]);
			actionStructural.doDeclareElevatorController(controllerid, firstelevatorid, secondelevatorid);

		} else {
			throw new RuntimeException("Parsing error for declareRudderController method");
		}
	}

	private void declareRudderController(String[] strarr) {
		if (this.syntaxValidator.declareSyntaxValidator(strarr)) {
			Identifier controllerid = new Identifier(strarr[3]);
			Identifier rudderid = new Identifier(strarr[6]);
			actionStructural.doDeclareRudderController(controllerid, rudderid);

		} else {
			throw new RuntimeException("Parsing error for declareRudderController method");
		}
	}

	private void declareCommit() {
		actionStructural.doCommit();
	}

	/////////////////////////////////////////////////////////////////////////////
	// BEHAVIORAL COMMANDS

	private void doBuilder(String[] strarr) {
		if(strarr.length >= 2) {
			if (strarr[0].equals("HALT")) {
				doHalt(strarr);
			}else {
				switch (strarr[2]) {
				case "DEFLECT":
					doDeflect(strarr);
					break;
				case "SPEED":
					doSpeed(strarr);
					break;
				case "SET":
					doSet(strarr);
					break;
				case "GEAR":
					doGear(strarr);
					break;
				default:
					throw new RuntimeException("Parsing error for doBuilder method");
				}
			}
		}else {
			throw new RuntimeException("Parsing error for doBuilder method");
		}
	}

	private void doHalt(String[] strarr) {
		if(strarr.length == 2) {
			if (ArgumentValidator.isValidIdentifier(strarr[1])) {
				Identifier id = new Identifier(strarr[1]);
				CommandDoHalt commandDoHalt = new CommandDoHalt(id);
				actionBehavioral.submitCommand(commandDoHalt);
			} else {
				throw new RuntimeException("Parsing error for doHalt method");
			}
		}else {
			throw new RuntimeException("Invalid Syntax for doHalt - must be 2 in length");
		}
		
	}

	private void doGear(String[] strarr) {
		if(strarr.length == 4) {
			Identifier id;
			boolean isDown;

			switch (strarr[3]) {
			case "DOWN":
				isDown = true;
				break;
			case "UP":
				isDown = false;
				break;
			default:
				throw new RuntimeException("Parsing error for doGear method");
			}

			if (ArgumentValidator.isValidIdentifier(strarr[1])) {
				id = new Identifier(strarr[1]);
			}else {
				throw new RuntimeException("Invalid Syntax for doGear");
			}
			CommandDoSelectGear commandDoSelectGear = new CommandDoSelectGear(id, isDown);
			actionBehavioral.submitCommand(commandDoSelectGear);
		}else {
			throw new RuntimeException("Invalid Syntax for doGear - must be 4 in length");
		}
	}

	private void doSet(String[] strarr) {
		if(strarr.length <= 7) {
			Identifier identifier = null, identifier2 = null;
			Power power = null;

			if (ArgumentValidator.isValidIdentifier(strarr[1]))
				identifier = new Identifier(strarr[1]);
			else {
				throw new RuntimeException("Invalid Syntax for doSet");
			}

			if (ArgumentValidator.isValidPower(strarr[4]))
				power = new Power(Double.parseDouble(strarr[4]));
			else {
				throw new RuntimeException("Invalid Syntax for doSet");
			}
			if(strarr.length == 7 ) {
				if (strarr[5].equals("ENGINE")) {
					if (ArgumentValidator.isValidIdentifier(strarr[6]))
						identifier2 = new Identifier(strarr[6]);
					else {
						throw new RuntimeException("Invalid Syntax for doSet");
					}

					CommandDoSetEnginePowerSingle commandDoSetEnginePowerSingle = new CommandDoSetEnginePowerSingle(identifier,
							power, identifier2);
					actionBehavioral.submitCommand(commandDoSetEnginePowerSingle);
				}
			}else if(strarr.length == 5){
				CommandDoSetEnginePowerAll commandDoSetEnginePowerAll = new CommandDoSetEnginePowerAll(identifier, power);
				actionBehavioral.submitCommand(commandDoSetEnginePowerAll);
			}else {
				throw new RuntimeException("Invalid Syntax for doSet - length incorrect");
			}
		}else {
			throw new RuntimeException("Invalid Syntax for doSet - must be at least 5 in length");
		}
	}

	private void doSpeed(String[] strarr) {
		if(strarr.length == 5) {
			if (strarr[3].equals("BRAKE")) {
				boolean isDeployed;

				if (strarr[4].equals("ON")) {
					isDeployed = true;

				} else if (strarr[4].equals("OFF")) {
					isDeployed = false;
				} else {
					throw new RuntimeException("Parsing error for doSpeed method");
				}
				Identifier id = null;
				if (ArgumentValidator.isValidIdentifier(strarr[1])) {
					id = new Identifier(strarr[1]);
				}else {
					throw new RuntimeException("Invalid Syntax for doSpeed");
				}
				CommandDoDeploySpeedBrake commandDoDeploySpeedBrake = new CommandDoDeploySpeedBrake(id, isDeployed);
				actionBehavioral.submitCommand(commandDoDeploySpeedBrake);
			} else {
				throw new RuntimeException("Parsing error for doSet method");
			}
		}else {
			throw new RuntimeException("Invalid Syntax for doSpeed - must be 5 in length");
		}
	}

	private void doDeflect(String[] strarr) {
		switch (strarr[3]) {
			case "RUDDER":
				doDeflectRudder(strarr);
				break;
			case "ELEVATOR":
				doDeflectElevator(strarr);
				break;
			case "AILERONS":
				doDeflectAilerons(strarr);
				break;
			case "FLAP":
				doDeflectFlaps(strarr);
				break;
		default:
			throw new RuntimeException("Parsing error for doDeflect method");
		}
	}

	private void doDeflectFlaps(String[] strarr) {
		if(strarr.length == 5) {
			Identifier identifier = null;
			Position position = null;

			if (ArgumentValidator.isValidIdentifier(strarr[1]))
				identifier = new Identifier(strarr[1]);
			else {
				throw new RuntimeException("Invalid Syntax for doDeflectFlaps");
			}

			switch (strarr[4]) {
			case "UP":
				position = new Position(Position.E_Position.UP);
				break;
			case "1":
				position = new Position(Position.E_Position.ONE);
				break;
			case "2":
				position = new Position(Position.E_Position.TWO);
				break;
			case "3":
				position = new Position(Position.E_Position.THREE);
				break;
			case "4":
				position = new Position(Position.E_Position.FOUR);
				break;
			default:
				throw new RuntimeException("Parsing error for doDeflectFlaps method");
			}

			CommandDoSetFlaps commandDoSetFlaps = new CommandDoSetFlaps(identifier, position);
			actionBehavioral.submitCommand(commandDoSetFlaps);
		}else {
			throw new RuntimeException("Invalid Syntax for doDeflectFlaps - must be 5 in length");
		}
	}

	private void doDeflectAilerons(String[] strarr) {
		if(strarr.length == 6) {
			Identifier identifier = null;
			Angle angle = null;
			boolean isDown;

			if (ArgumentValidator.isValidIdentifier(strarr[1]))
				identifier = new Identifier(strarr[1]);
			else {
				throw new RuntimeException("Invalid Syntax for doDeflectAilerons");
			}

			if (ArgumentValidator.isValidAngle(strarr[4]))
				angle = new Angle(Double.parseDouble(strarr[4]));
			else {
				throw new RuntimeException("Invalid Syntax for doDeflectAilerons");
			}

			if (strarr[5].equals("UP")) {
				isDown = false;
			} else if (strarr[5].equals("DOWN")) {
				isDown = true;
			} else {
				throw new RuntimeException("Parsing error for doDeflectAilerons method");
			}

			CommandDoDeflectAilerons commandDoDeflectAilerons = new CommandDoDeflectAilerons(identifier, angle, isDown);
			actionBehavioral.submitCommand(commandDoDeflectAilerons);
		}else {
			throw new RuntimeException("Invalid Syntax for doDeflectAilerons - must be 6 in length");
		}
	}

	private void doDeflectElevator(String[] strarr) {
		if(strarr.length == 6) {
			Identifier identifier = null;
			Angle angle = null;
			boolean isDown;

			if (ArgumentValidator.isValidIdentifier(strarr[1]))
				identifier = new Identifier(strarr[1]);
			else {
				throw new RuntimeException("Invalid Syntax for doDeflectElevator");
			}

			if (ArgumentValidator.isValidAngle(strarr[4]))
				angle = new Angle(Double.parseDouble(strarr[4]));
			else {
				throw new RuntimeException("Invalid Syntax for doDeflectElevator");
			}

			if (strarr[5].equals("DOWN")) {
				isDown = true;
			} else if (strarr[5].equals("UP")) {
				isDown = false;
			} else {
				throw new RuntimeException("Parsing error for doDeflectElevator method");
			}

			CommandDoDeflectElevator commandDoDeflectElevator = new CommandDoDeflectElevator(identifier, angle, isDown);
			actionBehavioral.submitCommand(commandDoDeflectElevator);
		}else {
			throw new RuntimeException("Invalid Syntax for doDeflectElevator - must be 6 in length");
		}
	}

	private void doDeflectRudder(String[] strarr) {
		if(strarr.length == 6) {
			Identifier identifier = null;
			Angle angle = null;
			boolean isRight;

			if (ArgumentValidator.isValidIdentifier(strarr[1]))
				identifier = new Identifier(strarr[1]);
			else {
				throw new RuntimeException("Invalid Syntax for doDeflectRudder");
			}

			if (ArgumentValidator.isValidAngle(strarr[4]))
				angle = new Angle(Double.parseDouble(strarr[4]));
			else {
				throw new RuntimeException("Invalid Syntax for doDeflectRudder");
			}
			
			if (strarr[5].equals("RIGHT")) {
				isRight = true;
			} else if (strarr[5].equals("LEFT")) {
				isRight = false;
			} else {
				throw new RuntimeException("Parsing error for doDeflectRudder method");
			}

			CommandDoDeflectRudder commandDoDeflectRudder = new CommandDoDeflectRudder(identifier, angle, isRight);
			actionBehavioral.submitCommand(commandDoDeflectRudder);
		}else {
			throw new RuntimeException("Invalid Syntax for doDeflectRudder - must be 6 in length");
		}
	}

	/////////////////////////////////////////////////////////////////////////////
	// MISCELLANEOUS COMMANDS
	// NOTE: All Syntax is checked in tokenizedCommandIsSyntacticallyCorrect()
	///////////////////////////////////////////////////////////////////////////// before
	///////////////////////////////////////////////////////////////////////////// entering
	///////////////////////////////////////////////////////////////////////////// these
	///////////////////////////////////////////////////////////////////////////// methods,
	///////////////////////////////////////////////////////////////////////////// so
	///////////////////////////////////////////////////////////////////////////// there
	///////////////////////////////////////////////////////////////////////////// is
	///////////////////////////////////////////////////////////////////////////// no
	///////////////////////////////////////////////////////////////////////////// need
	///////////////////////////////////////////////////////////////////////////// to
	///////////////////////////////////////////////////////////////////////////// check
	///////////////////////////////////////////////////////////////////////////// it

	private void miscClock(String[] strarr) {
		if (strarr.length == 2) {
			if (strarr[1].equals("PAUSE") || strarr[1].equals("RESUME") || strarr[1].equals("UPDATE")) {
				if (strarr[1].equals("PAUSE") || strarr[1].equals("RESUME")) {
					if (strarr[1].equals("PAUSE")) {
						CommandDoSetClockRunning commandDoSetClockRunning = new CommandDoSetClockRunning(true);
						actionMiscellaneous.submitCommand(commandDoSetClockRunning);
					} else {
						CommandDoSetClockRunning commandDoSetClockRunning = new CommandDoSetClockRunning(false);
						actionMiscellaneous.submitCommand(commandDoSetClockRunning);
					}

				} else {
					CommandDoClockUpdate commandDoClockUpdate = new CommandDoClockUpdate();
					actionMiscellaneous.submitCommand(commandDoClockUpdate);
				}

			} else if (!strarr[1].isEmpty()) {
				Rate rate = null;
				if (ArgumentValidator.isValidRate(strarr[1]))
					rate = new Rate(Integer.parseInt(strarr[1]));
				else {
					throw new RuntimeException("Invalid Rate");
				}
				CommandDoSetClockRate commandDoSetClockRate = new CommandDoSetClockRate(rate);
				actionMiscellaneous.submitCommand(commandDoSetClockRate);
			}else {
				throw new RuntimeException("Invalid Clock command call");
			}
		} else if (strarr.length == 1){
			CommandDoShowClock commandDoShowClock = new CommandDoShowClock();
			actionMiscellaneous.submitCommand(commandDoShowClock);
		}else {
			throw new RuntimeException("Invalid Clock command call");
		}
	}

	private void miscRun(String[] strarr) {
		String normal = "";
		for (int x = 0; x < strarr.length; x++) {
			normal += strarr[x];
			normal += " ";
		}
		String[] finalStrArr = new String[2];
		finalStrArr[0] = normal.substring(0, 4);
		finalStrArr[1] = normal.substring(5, normal.length());
		String fileToRun = finalStrArr[1].substring(1, finalStrArr[1].length() - 2);
		CommandDoRunCommandFile commandDoRunCommandFile = new CommandDoRunCommandFile(fileToRun);
		actionMiscellaneous.submitCommand(commandDoRunCommandFile);
	}

	private void miscExit(String[] strarr) {
		if (strarr.length == 1) {
			CommandDoExit commandDoExit = new CommandDoExit();
			actionMiscellaneous.submitCommand(commandDoExit);
		} else {
			throw new RuntimeException("Exit not called properly.");
		}
	}

	private void miscWait(String[] strarr) {
		Rate rate = new Rate(Integer.parseInt(strarr[1]));
		CommandDoWait commandDoWait = new CommandDoWait(rate);
		actionMiscellaneous.submitCommand(commandDoWait);
	}

}
