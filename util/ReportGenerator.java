package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {

    public static void generate(Path logsFolder, Path reportFile)
            throws IOException {

        long totalDeposits = 0;
        long totalWithdrawals = 0;

        List<String> fileDetails = new ArrayList<>();

        Files.createDirectories(logsFolder);
        Path reportAbsolutePath = reportFile.toAbsolutePath().normalize();

        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(logsFolder)) {

            for (Path file : stream) {
                if (!Files.isRegularFile(file)
                        || file.toAbsolutePath().normalize()
                                .equals(reportAbsolutePath)) {
                    continue;
                }

                BasicFileAttributes attributes = Files.readAttributes(
                        file, BasicFileAttributes.class);

                fileDetails.add(
                        "File: " + file.getFileName()
                        + " | Size: " + attributes.size() + " bytes"
                        + " | Last modified: "
                        + attributes.lastModifiedTime());

                try (BufferedReader reader = Files.newBufferedReader(
                        file, StandardCharsets.UTF_8)) {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.trim().split(",", 2);

                        if (parts.length != 2) {
                            continue;
                        }

                        try {
                            String type = parts[0].trim().toUpperCase();
                            long amount = Long.parseLong(parts[1].trim());

                            if (amount <= 0) {
                                continue;
                            }

                            if (type.equals("DEPOSIT")) {
                                totalDeposits += amount;
                            } else if (type.equals("WITHDRAWAL")) {
                                totalWithdrawals += amount;
                            }

                        } catch (NumberFormatException e) {
                            System.out.println(
                                    "Skipping invalid log entry: " + line);
                        }
                    }
                }
            }
        }

        Path parent = reportFile.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                reportFile, StandardCharsets.UTF_8)) {

            writer.write("===== END-OF-DAY BANK REPORT =====");
            writer.newLine();
            writer.write("Total deposits: Rs. " + totalDeposits);
            writer.newLine();
            writer.write("Total withdrawals: Rs. " + totalWithdrawals);
            writer.newLine();
            writer.newLine();
            writer.write("===== LOG FILE DETAILS =====");
            writer.newLine();

            for (String detail : fileDetails) {
                writer.write(detail);
                writer.newLine();
            }
        }
    }
}

