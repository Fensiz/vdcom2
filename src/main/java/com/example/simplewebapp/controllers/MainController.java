package com.example.simplewebapp.controllers;

import com.example.simplewebapp.helpers.CSVHelper;
import com.example.simplewebapp.models.CustomEntity;
import com.example.simplewebapp.repo.CustomEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {

	@Autowired private CustomEntityRepository repository;

	@GetMapping("/")
	public String root(Model model) {
		Iterable<CustomEntity> entities = repository.findAll();
		model.addAttribute("entities", entities);
		return "main_page";
	}
	@GetMapping("/add")
	public String addPage(Model model) {
		return "add_page";
	}
	@PostMapping("/add")
	public String addPagePost(
			@RequestParam String nameParam,
			@RequestParam String addressParam,
			@RequestParam String phoneParam,
			Model model) {
		CustomEntity entity = new CustomEntity(nameParam, addressParam, phoneParam);
		repository.save(entity);
		return "redirect:/";
	}

	@GetMapping("/{id}/edit")
	public String entityEdit(@PathVariable(value = "id") long id, Model model) {
		if (!repository.existsById(id)) {
			return "redirect:/";
		}
		Optional<CustomEntity> entity = repository.findById(id);
		ArrayList<CustomEntity> entities = new ArrayList<>();
		entity.ifPresent(entities::add);
		model.addAttribute("entities", entities);
		return "edit_page";
	}
	@PostMapping("/{id}/edit")
	public String entityEditPost(
			@PathVariable(value = "id") long id,
			@RequestParam String nameParam,
			@RequestParam String addressParam,
			@RequestParam String phoneParam,
			Model model) {
		CustomEntity entity = repository.findById(id).orElseThrow();
		entity.setName(nameParam);
		entity.setAddress(addressParam);
		entity.setPhone(phoneParam);
		repository.save(entity);
		return "redirect:/";
	}

	@GetMapping("/{id}/remove")
	public String entityDelete(@PathVariable(value = "id") long id, Model model) {
		repository.deleteById(id);
		return "redirect:/";
	}

	@GetMapping("/upload")
	public String csvUpload(Model model) {
		return "upload_page";
	}
	@PostMapping("/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file) {
		if (CSVHelper.hasCSVFormat(file)) {
			try {
				List<CustomEntity> entities = CSVHelper.csvToCustomEntities(file.getInputStream());
				repository.saveAll(entities);
			} catch (IOException e) {
				throw new RuntimeException("fail to store csv data: " + e.getMessage());
			}
		}
		return "redirect:/";
	}
}