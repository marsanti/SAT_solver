package utils;

import structure.Clause;
import structure.Formula;
import structure.Literal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Utils {
    public static Formula readFormulaFromFile(String path) {
        Formula formula = new Formula();

        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = data.trim(); // ignore useless space at the start and at the end of the line
                String[] data_split = data.split(" ");

                if(data_split[0].equals("c")) {
                    StringBuilder commentBuilder = new StringBuilder();
                    for(String piece : data_split) {
                        commentBuilder.append(piece);
                        commentBuilder.append(" ");
                    } 
                    String comment = commentBuilder.toString().trim();
                    if(data_split.length > 1) {
                        System.out.printf("comment found: %s%n", comment);
                    }
                } else if(data_split[0].equals("p")) {
                    formula.setNumberOfLiterals(Integer.parseInt(data_split[2]));
                    formula.setNumberOfClauses(Integer.parseInt(data_split[3]));
                    // System.out.printf("a %s problem with %s variables and %s clauses.%n", data_split[1], data_split[2], data_split[3]);
                } else {
                    Clause c = new Clause();
                    for(String litStr : data_split) {
                        int lit = Integer.parseInt(litStr);
                        if(lit != 0) {
                            Literal l = new Literal(Math.abs(lit), lit >= 0);
                            c.addLiteral(l);
                        }
                    }
                    formula.addClause(c);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Generic Error while reading the file with path: " + path + " with error: " + e.getMessage());
        }

        formula.initVSIDS();

        return formula;
    }
}
