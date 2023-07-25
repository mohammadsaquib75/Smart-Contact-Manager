package com.smart.smartcontactmanager.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.helper.Message;
import com.smart.smartcontactmanager.Entities.Contact;
import com.smart.smartcontactmanager.Entities.User;
import com.smart.smartcontactmanager.repoDao.ContactRepository;
import com.smart.smartcontactmanager.repoDao.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println("UserName: " + userName);

        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER: " + user);
        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normalUser/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normalUser/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
            Principal principal, HttpSession session) {
        try {
            String name = principal.getName();
            User user = userRepository.getUserByUserName(name);

            if (file.isEmpty()) {
                contact.setImage("contact.png");
            } else {
                contact.setImage(file.getOriginalFilename());
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
            }

            contact.setUser(user);
            user.getContacts().add(contact);
            userRepository.save(user);

            System.out.println("Data: " + contact);
            System.out.println("Added to database...");

            session.setAttribute("message", new Message("Your Contact is added!! Add next...", "success"));
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong...", "danger"));
        }

        return "normalUser/add_contact_form";
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
        model.addAttribute("title", "View Contact");

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);
        Pageable pageable = PageRequest.of(page, 10);
        Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);
        System.out.println("------------" + user.getId());

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());

        System.out.println("..................." + contacts);

        return "normalUser/show_contacts";
    }

    @GetMapping("/{cid}/contact")
    public String showContactDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {
        System.out.println(("CID: " + cid));

        Optional<Contact> contactOptional = contactRepository.findById(cid);
        Contact contact = contactOptional.get();

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }

        return "normalUser/contact_detail";
    }

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid, Model model, HttpSession session) {
        System.out.println("CID: " + cid);

        Contact contact = contactRepository.findById(cid).get();

        contact.setUser(null);
        contactRepository.delete(contact);

        System.out.println("DELETED");

        session.setAttribute("message", new Message("Contact deleted successfully.", "success"));

        return "redirect:/user/show-contacts/0";
    }

    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model model) {
        model.addAttribute("title", "Update Contact");
        Contact contact = contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
        return "normalUser/update_form";
    }
}
