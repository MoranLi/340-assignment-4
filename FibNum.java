package Cmpt340_T8.Cmpt340_T8;

import java.util.Objects;
/* File Name: Factorial.java
Author: David Kreiser
Class: CMPT 340 Tutorial 9
Contents: Basic file structure for calculating a factorial using Akka actors
*/
import java.util.Scanner;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

//One class contains entire program - sufficient for our purposes. Could be split into different files. 
public class FibNum {

	// Define static classes for any actors that will be needed
	private static class Worker extends UntypedActor {
		public void onReceive(Object message) {
			// Check what kind of message we received
			if(message instanceof Long){
				// Accept Long integer n
				Long n = (Long) message;
				Long m = new Long(0);
				// Base case: Set m to 1
				if(n==0){
					m= new Long(0);
				}							
				else if (n==1){
					m=new Long(1);
				}
				
				else{	
					// Else: Create a child actor to which we can delegate
					final ActorRef childworker = getContext().actorOf(Props.create(Worker.class)); 
					
					// "Ask" the child for the factorial of n - 1
					Timeout t = new Timeout(Duration.create(20, TimeUnit.SECONDS));
					Future<Object> future = Patterns.ask(childworker,new Long(n-1),t);
					Future<Object> future1 = Patterns.ask(childworker,new Long(n-2),t); 

					// Get result from child
					Long r = null;
					Long r1 = null;
					try{
						r = (Long)Await.result(future, t.duration());
						r1 = (Long)Await.result(future1, t.duration());
					}catch(Exception e){
						System.out.println("Got a timeout after waiting for 20 seconds for the value of"+(n-1));
					}
					
					// Calculate m = n * r
					m = new Long(m+r+r1);
				}
				// Send the value m to original sender
				getSender().tell(m, getSelf());
		}else{
			unhandled(message);
		}
	}

	public static void main(String[] args) {

		// Prompt the user for an integer
		System.out.print("Please enter value of x for which to calculate (x): ");
		Scanner in = new Scanner(System.in);
		Long num = new Long(in.nextInt());
		in.close();

		// Create an actor system
		final ActorSystem actorSystem = ActorSystem.create("actor-system");

		// Create a worker actor
		final ActorRef worker = actorSystem.actorOf(Props.create(Worker.class),"worker");
				
		// Create an inbox
		final Inbox inbox = Inbox.create(actorSystem);

		// Tell the worker to calculate the factorial of num
		inbox.send(worker, num);

		// Wait up to 20 seconds for a reply from the worker
		Long reply = null;
		try{
			reply = (Long)inbox.receive(Duration.create(20, TimeUnit.SECONDS));
		} catch (TimeoutException e){
			System.out.println("Got a time out after 20 seconds for the value of "+num+"i");
		}
		

		// Print the reply received
		System.out.println("the" +num +"`s fib num is "+ reply);
		// System.out.println(num + "! is " + (Long) reply);

		// Shut down the system gracefully
		actorSystem.terminate();
	}
	}
}