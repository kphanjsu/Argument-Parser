/*This is the first file
By: Christopher Burdette
    Nathan Chaney
    Kurtis Graben
    Khoi Phan
    Hui Wang
*/
package edu.jsu.mcis;

import java.util.*;
import java.io.*;

public class ArgumentParser{

  private Map<String,Argument> positionalArguments;
  private Map<String,Argument> namedArguments;
  private List<String> userPositionalArguments;
  private List<String> userNamedArguments;
  private Argument argument;
  private int helpMessage;
  private int missingArgumentHeightMessage;
  private int missingArgumentWidthAndHeightMessage;
  private int unrecognizedArgumentsMessage;
  private int incorrectTypeMessage;
  private String name;
  private String programName;
  private String programDescription;
  private Boolean helpMessageCalled;

	public ArgumentParser(){
		this.namedArguments=new LinkedHashMap<String,Argument>();
		this.positionalArguments=new LinkedHashMap<String,Argument>();
		this.userPositionalArguments=new ArrayList<String>(); // stores the pos. Args
		this.userNamedArguments=new ArrayList<String>(); // stores the named. Args
		this.helpMessage=0;
		this.missingArgumentWidthAndHeightMessage=0;
		this.missingArgumentHeightMessage=0;
		this.unrecognizedArgumentsMessage=0;
		this.incorrectTypeMessage=0;
		this.name="";
		this.helpMessageCalled = false;
	}

	public void addPositionalArgument(String x){
		Argument temp = new Argument();
		temp.setType(Argument.Type.STRING);
		temp.setPositionalName(x);
		positionalArguments.put(temp.getPositionalName(),temp);
	}

	public void addPositionalArgument(String x,Argument.Type t){
		Argument temp = new Argument();
		temp.setType(t);
		temp.setPositionalName(x);
		positionalArguments.put(temp.getPositionalName(),temp);
	}

	public void addNamedArgument(String x){
		Argument temp = new Argument();
		temp.setnamedArgumentName(x);
		namedArguments.put(x,temp);
	}

	private void checkUserInputSize(){
		if(userPositionalArguments.size()==1 && (!userPositionalArguments.contains("--h") && !userPositionalArguments.contains("-h"))){
				throw new MissingArgumentException("usage: java VolumeCalculator length width heigh. VolumeCalculator.java: error: the following arguments are required: width height");
		}
		else if(userPositionalArguments.size()==2){
			throw new MissingArgumentException("usage: java VolumeCalculator length width heigh. VolumeCalculator.java: error: the following arguments are required: width height");
		}
		else if(userPositionalArguments.size()>positionalArguments.size()){
			unrecognizedArgumentsMessage++;
				for(int m=3; m < userPositionalArguments.size(); m++){
					Argument temp= new Argument();
					temp.setUnrecognizedArgument(userPositionalArguments.get(m));
					throw new UnknownArgumentException("usage: java VolumeCalculator length width height" + "\n" + "VolumeCalcultor.java: error: unrecognized arguments: " + temp.getUnrecognizedArgument());
				}
		 }
	}

	private void matchNamedArguments(){
		for(String s: namedArguments.keySet()){
			Argument temp=namedArguments.get(s);
			if(temp.getnamedArgumentName()=="Type"  && !userNamedArguments.contains("t")){
				String box="Box";
				temp.setNamedArgumentValue(box);
				namedArguments.put(s,temp);
			}
			else if(temp.getnamedArgumentName()=="Digits" && !userNamedArguments.contains("d")){
				String digit="4";
				temp.setNamedArgumentValue(digit);
				namedArguments.put(s,temp);
			}
		}
		int j=1;
		for(int i=0; i < userNamedArguments.size(); i+=2){
			Argument temp2=new Argument();
			if((i % 2==0) && (j % 2==1)){
				temp2.setnamedArgumentName(userNamedArguments.get(i));
				temp2.setNamedArgumentValue(userNamedArguments.get(j));
				namedArguments.put(userNamedArguments.get(i),temp2);
				j+=2;
			}
		}
	}

	private void matchPositionalArguments(){
		int i=0;

	 	if(userPositionalArguments.size()==positionalArguments.size()){
			for(String s: positionalArguments.keySet()){
				Argument temp=positionalArguments.get(s);
				if(temp.getType()==Argument.Type.INT){
					temp.setPositionalValue(userPositionalArguments.get(i));
					try {
						Integer.parseInt(temp.getPositionalValue());
					}
					catch(NumberFormatException ex) {
						temp.setIncorrectType(temp.getPositionalValue());
						setIncorrectTypeMessage(temp.getIncorrectType());
						throw new IncorrectDataTypeException("usage: java "+ getProgramName() + " length width height\nVolumeCalcultor.java: error: argument width: invalid Integer value: " + temp.getIncorrectType());
					}
					i++;
				}
				else if(temp.getType()==Argument.Type.FLOAT){
					temp.setPositionalValue(userPositionalArguments.get(i));
					try {
						Float.parseFloat(temp.getPositionalValue());
					}
					catch(NumberFormatException ex){
						incorrectTypeMessage++;
						temp.setIncorrectType(temp.getPositionalValue());
						setIncorrectTypeMessage(temp.getIncorrectType());
						throw new IncorrectDataTypeException("usage: java "+ getProgramName() + " length width height\nVolumeCalcultor.java: error: argument width: invalid float value: " + temp.getIncorrectType());
					}
					i++;
				}
				else if(temp.getType()==Argument.Type.BOOLEAN){
					temp.setPositionalValue(userPositionalArguments.get(i));
					i++;
				}

				else{
					temp.setPositionalValue(userPositionalArguments.get(i));
					i++;
				}
			}
		}
	}

	public void parse(String[] args) {
		for(int i=0; i<args.length; i++){
			userPositionalArguments.add(args[i]);
		}
		if(userPositionalArguments.contains("--h") ||userPositionalArguments.contains("-h") || userPositionalArguments.contains("--help") ){
				userPositionalArguments.remove("--h");
				userPositionalArguments.remove("--help");
				userPositionalArguments.remove("-h");
				helpMessageCalled = true;
		}
		for(int j=0; j < userPositionalArguments.size(); j++){
			int k=0;
			while(userPositionalArguments.get(j).charAt(k)=='-'){
				userNamedArguments.add(userPositionalArguments.get(j).substring(userPositionalArguments.get(j).lastIndexOf("-")+1));
				userNamedArguments.add(userPositionalArguments.get(j+1));
				userPositionalArguments.remove(userPositionalArguments.get(j+1));
				userPositionalArguments.remove(userPositionalArguments.get(j));
				j=0;
			}
		}
		checkUserInputSize();
		matchPositionalArguments();
		matchNamedArguments();
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String name){
		if(positionalArguments.get(name) != null){
			Argument temp=new Argument();
			temp= positionalArguments.get(name);
			if(temp.getType()==Argument.Type.INT){
				int num=Integer.parseInt(temp.getPositionalValue());
				return (T) new Integer (num);
			}
			else if(temp.getType()==Argument.Type.FLOAT){
				float num=Float.parseFloat(temp.getPositionalValue());
				return (T) new Float (num);
			}
			else if(temp.getType()==Argument.Type.BOOLEAN){
				boolean num=Boolean.parseBoolean(temp.getPositionalValue());
				return (T) Boolean.valueOf(num);
			}
			else{
				return (T) new String(temp.getPositionalValue());
			}
		}
		for(int i=0; i < userNamedArguments.size(); i++){
			if(userNamedArguments.get(i).equals(name.substring(0,1).toLowerCase()) || userNamedArguments.get(i).equals(name.substring(0,1))){
				return (T) new String(userNamedArguments.get(i+1));
			}
		}
		Argument temp= new Argument();
		temp=namedArguments.get(name);
		return (T) temp.getNamedArgumentValue();
	}

 	protected int getSizeOfHashMap(){
 		return positionalArguments.size();
 	}

	protected String getProgramName(){
		return programName;
	}

	public void assignProgramName(String name){
		programName = name;
	}

	public void assignProgramDescription(String description) {
		programDescription = description;
	}

	protected String getProgramDescription() {
		return programDescription;
	}

 	public String getHelpMessage(){
 		return "usage: java " + getProgramName() + " length width height" + "\n" + getProgramDescription() + "\n" + "positional arguments:" + "\n" +   "length the length of the box (float)"  + "\n" +   "width the width of the box(float)" + "\n" + "height the height of the box(float)";
 	}

 	private void setIncorrectTypeMessage(String s){
 		name=s;
 	}

  public boolean isHelpMessageCalled(){
 		return helpMessageCalled;
 	}
}
