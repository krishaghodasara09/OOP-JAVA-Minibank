package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import model.Account;

public class StatePersister {

    public static void save(Account[] accounts, Path file) {
        try {
            Path parent = file.toAbsolutePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream(file.toFile()))) {
                out.writeObject(accounts);
            }

            System.out.println("Accounts saved successfully.");

        } catch (IOException e) {
            throw new UncheckedIOException("Could not save accounts.", e);
        }
    }

    public static Account[] load(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file.toFile()))) {

            return (Account[]) in.readObject();

        } catch (IOException e) {
            throw new UncheckedIOException("Could not load accounts.", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Account class could not be found while loading.", e);
        }
    }
}

