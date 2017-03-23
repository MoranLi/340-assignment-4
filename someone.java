package Cmpt340_T8.Cmpt340_T8;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import Cmpt340_T8.Cmpt340_T8.SingleLaneBridge.BridgeActor;
import Cmpt340_T8.Cmpt340_T8.SingleLaneBridge.CarActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

public class someone {	
	
	/*
	
	public ActorSystem system;
	
	public ActorRef some;
	
	String reftoGeneral;
	
	*/
	
	public static LinkedList<pair> agoodlist = new LinkedList<pair>();
	
	public static class pair{
		int male;
		int female;
		public pair(int a1,int a2){
			male=a1;
			female=a2;
		}
		public String toString(){
			return male + " " + female + "\n";
		}
	}
	
	/*
	public static class GeneralControl extends UntypedActor{
		
		
		int [][] menmarks;
		
		int [][] femalemarks;
		
		LinkedList<pair> resultlist;

		ActorSystem system;
		
		@Override
		public void onReceive(Object arg0) throws Throwable {
			// TODO Auto-generated method stub
			
			
			if(arg0 instanceof pair){
				
				resultlist.add((pair)arg0);
				
				if(resultlist.size()==menmarks.length){
				
					System.out.print(resultlist.toString());
					
					system.terminate();
				
				}
				
			}
			
			else if (arg0 instanceof int[][]){
				
				
				try{
					//System.out.println("start add men mark");
					int a = menmarks.length;
				}
				catch (Exception e){
					menmarks = (int [][])arg0;
					return;
				}
				//System.out.println("start add women mark");
				
				femalemarks = (int [][])arg0;
				
				LinkedList<pair> resultlist = new LinkedList<pair>();
				
				ActorSystem system = ActorSystem.create("marrageProblem");
				
				final Inbox input = Inbox.create(system);
				
				ActorRef[] men = new ActorRef[menmarks.length];
				ActorRef[] women = new ActorRef[menmarks.length];
				
				//System.out.println("start create men and women");
				
				for(int i=0;i<menmarks.length;i++){
					
					men[i] = system.actorOf(Props.create(ManActor.class), "man-"+i);
					
					women[i]  = system.actorOf(Props.create(FemaleActor.class), "Female-"+i);
					
				}
				
				//System.out.println("finish start create men and women");
				
				for(int i=0;i<menmarks.length;i++){
					
					//System.out.println("start send");
					
					input.send(men[i], i);
					input.send(men[i], menmarks[i]);
					input.send(women[i], i);
					input.send(women[i], femalemarks[i]);
					
					
				}
				
				try {
					Thread.sleep((2 * 1000));
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
				
			}
			
			else{
				
				unhandled(arg0);
				
			}
			
		}
		
	}
	
	*/
	public static class ManActor extends UntypedActor{
		
		ActorRef general;	
		
		boolean single = true;
		
		int malecode;
		
		int resultfemale;
		
		int[] femalecode;
		
		int num = 0;
			

		@Override
		public void onReceive(Object message) throws Throwable {
			
			if(message instanceof pair){
				
				single = false;
				
				resultfemale = ((pair)message).female;
				
				//System.out.println("men win marrige request"+(((pair)message).toString()));
				
				//System.out.println(getContext().actorSelection("akka://SingleLaneBridgeProblem/user/www"));
				
				//getContext().actorSelection("akka://SingleLaneBridgeProblem/user/www").tell(message, getSelf());
				
				agoodlist.add((pair)message);
				
				System.out.println(((pair)message).toString());
				
				//getContext().actorSelection(general.path().name()).tell(((pair)message),getSelf());
				
			}
			else if (message instanceof String){
				
				num++;
				
				int i = femalecode[num] -1;
				
				//System.out.println("men send marrige request"+(new pair(malecode,i).toString()));
				
				getContext().actorSelection("/user/Female-"+i).tell(new pair(malecode,i), getSelf());
				
				
			}
			else if (message instanceof Integer){
				
				//System.out.println("receive selfcode");
				
				malecode = (Integer)message;
				
				general = getSender();				
				
			}
			else if (message instanceof int[]){
				
				//System.out.println("receive own mark");
				
				femalecode = (int [])message;
				
				int i = femalecode[num] -1;
				
				//System.out.println(getContext().actorSelection("/user/Female-"+i));
				
				getContext().actorSelection("/user/Female-"+i).tell(new pair(malecode,i), getSelf());
				
				num++;
				
				
			}
			else{
				
				unhandled(message);
				
			}
			
		}
		
	}
	
	public static class FemaleActor extends UntypedActor {
		
		
		
		boolean single = true;
		
		int femalecode;
		
		int resultmale;
		
		int[] malecode;
		
		
		public int getMaleIndex(int male){
			
			for(int i=0;i<malecode.length;i++){
				
				if(malecode[i] == male){
					
					return i;
				}
				
			}
			
			return -1;
			
		}

		@Override
		public void onReceive(Object message) throws Throwable {
			
			if(message instanceof pair){
				
				//System.out.println("reveive female request");
				
				if(((pair)message).female==femalecode){
					
					//System.out.println("reveive female request");
				
					if(single = true){
				
						single = false;
				
						resultmale = ((pair)message).female;
					
						getSender().tell(message, sender());
						
						agoodlist.add((pair)message);
				
					}
				
					else{
					
					
						if(getMaleIndex(((pair)message).male)<getMaleIndex(resultmale)){
						
							getSender().tell("No", sender());
						
						}
					
					
						else{
						
							getSender().tell(message, sender());
						
							resultmale=((pair)message).male;	
							
							agoodlist.add((pair)message);
						
						}									
				
				
					}
				
				}					
				
				
				else{
				
					unhandled(message);
				
				}
				
			}
			
			else if (message instanceof Integer){
				
				femalecode = (Integer)message;
				
			}
			else if (message instanceof int[]){
				
				malecode = (int [])message;
				
			}
			else{
				
				unhandled(message);
				
			}
			
		}
	
	}
	
	public static void main(String argc[]){
		
		int [][] men = {{1,2,3,4},{2,3,4,1},{3,4,1,2},{4,1,2,3}};
		
		int [][] women = {{4,3,2,1},{3,2,1,4},{2,1,4,3},{1,4,3,2}};
		
		int [][] men2 = {{1,2,3,4},{3,4,1,2},{4,1,2,3},{2,3,4,1}};
		
		int [][] women2 = {{4,3,2,1},{2,1,4,3},{3,2,1,4},{1,4,3,2}};
		
		ActorSystem system = ActorSystem.create("marrageProblem");
		
		final Inbox input = Inbox.create(system);
		
		ActorRef[] mens = new ActorRef[men.length];
		
		ActorRef[] womens = new ActorRef[women.length];
		
		//System.out.println("start create men and women");
		
		for(int i=0;i<mens.length;i++){
			
			mens[i] = system.actorOf(Props.create(ManActor.class), "man-"+i);
			
			womens[i]  = system.actorOf(Props.create(FemaleActor.class), "Female-"+i);
			
		}
		
		//System.out.println("finish start create men and women");
		
		for(int i=0;i<mens.length;i++){
			
			//System.out.println("start send");
			
			input.send(mens[i], i);
			input.send(mens[i], men2[i]);
			input.send(womens[i], i);
			input.send(womens[i], women2[i]);
			
			
		}
		
		// the rest of code just keep function run but not have effect
		
		
		// Wait up to 20 seconds for a reply from the worker
			pair reply = null;
			try{
				reply = (pair)input.receive(Duration.create(2, TimeUnit.SECONDS));
			} catch (TimeoutException e){
				//System.out.println("Got a time out after 20 seconds for the value of result");
			}
				

			// Print the reply received
			//System.out.println( reply);
			// System.out.println(num + "! is " + (Long) reply);

			// Shut down the system gracefully
			
			system.terminate();
		
		
	}
	
}

