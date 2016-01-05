package me.tlamb96;

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

import me.tlamb96.Scraper.Student;

public class Main {

	public static void main(String[] args) throws Exception {
		if( args.length != 1 )
			throw new IllegalArgumentException();
		Scanner sc;
		try { 
			sc = new Scanner( new File( args[0] ) ); 
		}
		catch( Exception e ) { 
			throw new Exception( String.format("Could not open file: %s properly!", args[0]) ); 
		}
		// read the Census file, scrape the names from the web site, save in arraylist
		Scraper scraper = new Scraper();
		LinkedList<Student> students = scraper.scrape( sc );
		for(Student std : students )
			System.out.println(std);
		// generate emails and save in json file
		EmailGen gen = new EmailGen();
		gen.generate( students );
		gen.write();
	}

}
