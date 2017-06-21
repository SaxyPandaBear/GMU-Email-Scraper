package me.tlamb96;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.jaunt.*;

public class Scraper {

    public class Student {
        private String first, middleInit, last, major;

        public String getFirst() {
            return first;
        }

        public String getMiddleInit() {
            return middleInit;
        }

        public String getLast() {
            return last;
        }

        public String getMajor() {
            return major;
        }

        public void setFirst(String s) {
            first = s;
        }

        public void setMiddleInit(String s) {
            middleInit = s;
        }

        public void setLast(String s) {
            last = s;
        }

        public void setMajor(String s) {
            major = s;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(first);
            sb.append(" ");
            if (middleInit != null) {
                sb.append(middleInit);
                sb.append(" ");
            }
            sb.append(last);
            return sb.toString();
        }
    }

    public LinkedList<Student> scrape(Scanner sc) {

        System.out.println("Starting the scraper...");
        LinkedList<Student> students = new LinkedList<>();
        while (sc.hasNext()) {
            String name = sc.next();
            System.out.println(String.format("Searching for students with the first name %s...", name));
            try {
                // search and scrape all pages
                boolean nextPg = true;
                int pgNum = 1;
                String officialLink = null;
                final String urlHead = "http://peoplefinder.gmu.edu/index.php?search=";
                final String urlTail = "&group=students&x=63&y=7";
                UserAgent userAgent = new UserAgent();
                userAgent.visit(urlHead + name + urlTail);
                do {
                    if (pgNum != 1)
                        userAgent.visit(officialLink);
                    List<Element> elems = userAgent.doc.findEach("<div class=\"person (even|odd)\">").toList();
                    if (elems.size() == 0) // no people found
                        break;
                    for (Element elem : elems) {
                        Student student = new Student();
                        String[] split1 = elem.findFirst("<h3>").getText().split(", ");
                        if (!(split1.length == 1 || (split1[1].length() == 2 && (split1[1].charAt(1) > 64 && split1[1].charAt(1) < 94))))
                            continue;
                        students.add(student);
                        student.setLast(split1[0]);
                        String[] split2;
                        if (split1.length == 2) { // has middle initial
                            split2 = split1[1].split(" ");
                            student.setFirst(elem.findFirst("<span>").getText() + split2[0]);
                        } else {
                            split2 = null;
                            student.setFirst(elem.findFirst("<span>").getText());
                        }
                        if (split2 != null && split2.length == 2) { // if there is not a last name
                            student.setMiddleInit(split2[1]);
                            System.out.println(String.format("Adding %s %s %s...", student.getFirst(),
                                    student.getMiddleInit(), student.getLast()));
                        } else {
                            student.setMiddleInit(null);
                            System.out.println(String.format("Adding %s %s...", student.getFirst(), student.getLast()));
                        }
                    }
                    try {
                        Element linkElem = userAgent.doc.findFirst("<li class=\"next\">");
                        List<String> s = linkElem.findAttributeValues("<a href>");
                        String str = s.get(0);
                        String[] theStr = str.split("\\?");
                        officialLink = theStr[0] + "index.php?" + theStr[1];
                        officialLink = officialLink.replaceAll("&amp;", "&");
                        pgNum++;
                    } catch (NotFound e) {
                        nextPg = false;
                    }
                    // make scraper wait 2 seconds before going to next page (reduce network traffic)
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                } while (nextPg);
            } catch (JauntException e) {         //if an HTTP/connection error occurs, handle JauntException.
                System.err.println(e);
            }
        }
        return students;
    }
}
