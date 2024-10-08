package utils;

import ea.Individual;
import pso.Particle;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Logger {

    public Logger() { }

    private void loadIntoFile(String filename, String[] data) {
        File csv = new File(filename);
        try {
            if (csv.createNewFile()){
                try (PrintWriter pw = new PrintWriter(csv)) {
                    for (String line : data)
                        pw.println(line);
                }
            } else {
                try (FileWriter pw = new FileWriter(csv, true)) {
                    for (String line : data)
                        pw.append(line);
                    pw.append("\n");
                    pw.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n\n\n\nDID YOU CLOSE THE FILE??? YEA I DIDN'T THINK SO.\n\n\n\n");
        }
    }

    public void loadIntoFile(String filename, double[] bests, double[] worsts, double[] avgs) {
        int noOfGenerations = bests.length;
        String[] data = new String[noOfGenerations];
        for (int i = 0; i < noOfGenerations; i++)
            data[i] = i + ";" + bests[i] + ";" + worsts[i] + ";" + avgs[i] + ";";
        loadIntoFile(filename, data);
    }

    public void loadIntoFileEA(String filename, ArrayList<Individual> bests, ArrayList<Individual> worsts, ArrayList<Individual> avgs) {
        int noOfGenerations = bests.size();
        String[] data = new String[noOfGenerations];
        for (int i = 0; i < noOfGenerations; i++)
            data[i] = i + ";" + bests.get(i).getFitness() + ";" + worsts.get(i).getFitness() + ";" + avgs.get(i).getFitness() + ";";
        loadIntoFile(filename, data);
    }

    public void loadIntoFilePSO(String filename, ArrayList<Particle> bests, ArrayList<Particle> worsts, ArrayList<Particle> avgs) {
        int noOfGenerations = bests.size();
        String[] data = new String[noOfGenerations];
        for (int i = 0; i < noOfGenerations; i++)
            data[i] = i + ";" + bests.get(i).getFitness() + ";" + worsts.get(i).getFitness() + ";" + avgs.get(i).getFitness() + ";";
        loadIntoFile(filename, data);
    }

    public void loadIntoFile(String filename, String params, double best, double worst, double avg, double std, double time) {
        File csv = new File(filename);
        try {
            if (csv.createNewFile()) {
                try (PrintWriter pw = new PrintWriter(csv)) {
                    pw.println(best + ";" + worst + ";" + avg + ";" + std + ";" + time + ";" + params + ";");
                }
            } else {
                try (FileWriter pw = new FileWriter(csv, true)) {
                    pw.append(String.valueOf(best));
                    pw.append(";");
                    pw.append(String.valueOf(worst));
                    pw.append(";");
                    pw.append(String.valueOf(avg));
                    pw.append(";");
                    pw.append(String.valueOf(std));
                    pw.append(";");
                    pw.append(String.valueOf(time));
                    pw.append(";");
                    pw.append(params);
                    pw.append(";");
                    pw.append("\n");
                    pw.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n\n\n\nDID YOU CLOSE THE FILE??? YEA I DIDN'T THINK SO.\n\n\n\n");
        }
    }

}
