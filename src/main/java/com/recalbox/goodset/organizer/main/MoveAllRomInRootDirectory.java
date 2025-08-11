package com.recalbox.goodset.organizer.main;

import com.recalbox.goodset.organizer.main.init.RomOrganizer;
import com.recalbox.goodset.organizer.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static com.recalbox.goodset.organizer.main.init.CommandLineRunnerInitializer.createRomOrganizer;
import static com.recalbox.goodset.organizer.util.UncheckedIOExceptionThrower.rethrowIOException;

@RequiredArgsConstructor
@Log
public class MoveAllRomInRootDirectory {

    private final RomOrganizer romOrganizer;

    public static void main(String... args) {
        new MoveAllRomInRootDirectory(createRomOrganizer(args))
                .moveAllRomsInParentRomDirectory();
    }

    public void moveAllRomsInParentRomDirectory() {
        FileUtils.listDirectories(romOrganizer.romDirectory)
                .forEach(this::moveAllRomsInParentRomDirectory);
    }

    private void moveAllRomsInParentRomDirectory(Path subDirectory) {
        rethrowIOException(() -> Files.walkFileTree(subDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                Files.move(path, romOrganizer.romDirectory.resolve(path.getFileName()));
                return super.visitFile(path, basicFileAttributes);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
                Files.delete(path);
                log.info(String.format("Subdirectory '%s' processed", path));
                return super.postVisitDirectory(path, e);
            }
        }));
    }
}
