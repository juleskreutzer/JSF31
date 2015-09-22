/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opdrachten.week.pkg4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.gc;
import java.util.logging.Level;
import java.util.logging.Logger;
import timeutil.TimeStamp;

/**
 *
 * @author Rick van Duijnhoven & Jules Kreutzer
 */
public class OpdrachtenWeek4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        TimeStamp ts = new TimeStamp();
        ts.setBegin("Begin van het programma");
        Runtime run = java.lang.Runtime.getRuntime();
        System.out.println(run.availableProcessors()); //prints available processors
        System.out.println(run.totalMemory());//prints memory currently available to process
        System.out.println(run.maxMemory());//prints max memory available.
        System.out.println(run.freeMemory());//prints free memory available.
        System.out.println(run.totalMemory() - run.freeMemory());//prints amount of memory process is using
        
        String s;
        for(int i=0; i<100000; i++) {
        s = "Hello "+i;
        }
        System.out.println("Memory currently being used by this process, after the loop:");
        System.out.println(run.totalMemory() - run.freeMemory());
        gc();//Garbage collector.
        System.out.println("Memory currently being used by this process, after the garbage collector:");
        System.out.println(run.totalMemory() - run.freeMemory());
        
        //Nr 5: start processes from this application, first with Processbuilder, next with runtime.exec
        TimeStamp tspb = new TimeStamp();
        tspb.setBegin("Begin met gnome-calculator door ProcessBuilder");
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("gnome-calculator");
        Process pr = null;
        try {
            pr = pb.start();
           
        } catch (IOException ex) {
            Logger.getLogger(opdrachten.week.pkg4.OpdrachtenWeek4.class.getName()).log(Level.SEVERE, null, ex);
        }
        tspb.setEnd("We gaan nu slapen");
        Thread.sleep(5000);
        pr.destroy();//stops/destroys the process.
        tspb.setEnd("Process is weg");
        Thread.sleep(2000);
        
        TimeStamp tsrt = new TimeStamp();
        tsrt.setBegin("Begin met gnome-calculator door Runtime");
        try {
            pr = run.exec("gnome-calculator");
        } catch (IOException ex) {
            Logger.getLogger(opdrachten.week.pkg4.OpdrachtenWeek4.class.getName()).log(Level.SEVERE, null, ex);
        }
        tsrt.setEnd("En we gaan weer slapen");
        Thread.sleep(3000);
        pr.destroy();
        tsrt.setEnd("Process is weg");
        
        //Nr 6 below, input/output streams of a process
        try {
            pr = run.exec("ls");
        } catch (IOException ex) {
            Logger.getLogger(opdrachten.week.pkg4.OpdrachtenWeek4.class.getName()).log(Level.SEVERE, null, ex);
        }
        InputStream is = pr.getInputStream();//gets the input out of the process
        InputStreamReader isr = new InputStreamReader(is);//reads the given input
        BufferedReader br = new BufferedReader(isr);

        String line;
        try {
            while ( (line = br.readLine()) != null ) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(opdrachten.week.pkg4.OpdrachtenWeek4.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(opdrachten.week.pkg4.OpdrachtenWeek4.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        ts.setEnd("Einde van het programma");
        System.out.print(tspb.toString());
        System.out.print(tsrt.toString());
        System.out.print(ts.toString());

    }

}
