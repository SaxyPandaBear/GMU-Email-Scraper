package me.tlamb96;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import me.tlamb96.Scraper.Student;

public class EmailGen {

    private JSONObject out;

    public EmailGen() {
        out = new JSONObject();
    }

    @SuppressWarnings("unchecked")
    public void generate(LinkedList<Student> students) {
        for (Student student : students) {
            JSONObject theStudent = new JSONObject();
            theStudent.put("Full Name", student.toString());
            theStudent.put("First Name", student.getFirst());
            theStudent.put("Middle Initial", student.getMiddleInit());
            theStudent.put("Last Name", student.getLast());
            // generate emails
            final String tail = "@masonlive.gmu.edu";
            JSONArray emails = new JSONArray();
            char firstLetter = student.getFirst().charAt(0);
            String lastLetters;
            if (student.getLast().length() > 7)
                lastLetters = student.getLast().substring(0, 8);
            else
                lastLetters = student.getLast();
            emails.add(firstLetter + lastLetters + tail);
            if (1 + lastLetters.length() == 7)
                for (int i = 0; i < 10; i++)
                    emails.add(firstLetter + lastLetters + i + tail);
            if (1 + lastLetters.length() < 7)
                for (int i = 0; i < 100; i++)
                    emails.add(firstLetter + lastLetters + i + tail);
            theStudent.put("email", emails);
            out.put("Student", theStudent);
        }
    }

    public void write() throws IOException {
        System.out.println("Writing to Students.json...");
        FileWriter fw = new FileWriter("Students.json");
        fw.write(out.toJSONString());
        fw.flush();
        System.out.println("Finished writing...");
        fw.close();
    }

}
