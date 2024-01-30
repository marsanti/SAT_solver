package utils;

import structure.Clause;
import structure.Formula;
import structure.Literal;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    /**
     * Read the content of a file and convert it into a Formula object.
     * @param path path to file
     * @return Formula object
     */
    public static Formula readFormulaFromFile(String path) {
        Formula formula = new Formula();

        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = data.trim(); // ignore useless space at the start and at the end of the line
                String[] data_split = data.split("\\s+");
                if(data.contains("%") && !data_split[0].equals("c")) {
                    break;
                }
                if(data_split[0].equals("c")) {
                    StringBuilder commentBuilder = new StringBuilder();
                    for(String piece : data_split) {
                        commentBuilder.append(piece);
                        commentBuilder.append(" ");
                    } 
                    String comment = commentBuilder.toString().trim();
//                    if(data_split.length > 1) {
//                        System.out.printf("comment found: %s%n", comment);
//                    }
                } else if(data_split[0].equals("p")) {
                    formula.setNumberOfLiterals(Integer.parseInt(data_split[2]));
                    formula.setNumberOfClauses(Integer.parseInt(data_split[3]));
                    // System.out.printf("a %s problem with %s variables and %s clauses.%n", data_split[1], data_split[2], data_split[3]);
                } else {
                    Clause c = new Clause();
                    for(String litStr : data_split) {
                        int lit = Integer.parseInt(litStr);
                        if(lit != 0) {
                            Literal l = new Literal(Math.abs(lit), lit >= 0, -1);
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

    /**
     * Fetch all files in a specific folder.
     * @param pathToFolder that contains .cnf files
     * @return ArrayList of String containing file paths
     * @throws Exception if pathToFolder is null or empty
     */
    public static ArrayList<String> readFilesFromFolder(String pathToFolder) throws Exception {
        if(pathToFolder == null || pathToFolder.isEmpty()) {
            throw new Exception("No path to folder found!");
        }

        return Stream.of(Objects.requireNonNull(new File(pathToFolder).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getPath).collect(Collectors.toCollection(ArrayList::new));
    }

    public static void saveOutputContent(String filepath, String output) throws IOException {
        File outputDirectory = new File("output_tests");
        if(!outputDirectory.exists()) {
            assert outputDirectory.mkdir();
        }

        File testFile = new File(filepath);
        File outFile = new File(outputDirectory, testFile.getName());
        assert outFile.exists() || outFile.createNewFile();

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile.getPath()), StandardCharsets.UTF_8))) {
            writer.write(output);
        }
        catch (IOException ex) {
            // Handle me
            System.out.println("something went wrong writing the output file: " + ex.getMessage());
        }
    }
}
