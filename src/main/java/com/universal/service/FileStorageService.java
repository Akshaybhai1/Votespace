package com.universal.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.universal.exception.AppException;

/** Saves uploaded photos to disk (outside the war) and serves them via /uploads/**. */
@Service
public class FileStorageService {

	private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
	private static final long MAX_BYTES = 5L * 1024 * 1024;

	private final Path root;

	public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) throws IOException {
		this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(this.root);
	}

	public Path getRoot() {
		return root;
	}

	/** Stores the image and returns the generated file name, or null when nothing was uploaded. */
	public String store(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		if (file.getSize() > MAX_BYTES) {
			throw new AppException("The image is too large. Maximum size is 5 MB.");
		}
		try {
			String ext = detectExtension(file);
			if (ext == null) {
				throw new AppException("Please upload a valid image (JPG, PNG, GIF or WEBP).");
			}
			String name = UUID.randomUUID() + "." + ext;
			try (InputStream in = file.getInputStream()) {
				Files.copy(in, root.resolve(name), StandardCopyOption.REPLACE_EXISTING);
			}
			return name;
		} catch (IOException e) {
			log.error("Could not save upload", e);
			throw new AppException("The image could not be saved. Please try again.");
		}
	}

	public void delete(String fileName) {
		if (fileName == null || fileName.isBlank()) {
			return;
		}
		try {
			Path target = root.resolve(fileName).normalize();
			if (target.startsWith(root)) {
				Files.deleteIfExists(target);
			}
		} catch (IOException e) {
			log.warn("Could not delete {}", fileName, e);
		}
	}

	/** Checks the real file signature (not the file name the browser claims). */
	private String detectExtension(MultipartFile file) throws IOException {
		byte[] h = new byte[12];
		int n;
		try (InputStream in = file.getInputStream()) {
			n = in.readNBytes(h, 0, h.length);
		}
		if (n >= 3 && (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF) {
			return "jpg";
		}
		if (n >= 8 && (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G') {
			return "png";
		}
		if (n >= 4 && h[0] == 'G' && h[1] == 'I' && h[2] == 'F' && h[3] == '8') {
			return "gif";
		}
		if (n >= 12 && h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F' && h[8] == 'W' && h[9] == 'E'
				&& h[10] == 'B' && h[11] == 'P') {
			return "webp";
		}
		return null;
	}
}
