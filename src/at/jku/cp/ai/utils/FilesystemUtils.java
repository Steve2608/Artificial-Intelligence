package at.jku.cp.ai.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FilesystemUtils {
	public static void rmrfMkdir(final String dir) {
		try {
			if (Files.exists(Paths.get(dir))) {
				Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}
				});
				Files.delete(Paths.get(dir));
			}

			Files.createDirectory(Paths.get(dir));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String join(final Object... parts) {
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < parts.length; i++) {
			final String s = parts[i].toString();
			sb.append(s);
			if (i < parts.length - 1) {
				sb.append(File.separator);
			}
		}

		return sb.toString();
	}
}
