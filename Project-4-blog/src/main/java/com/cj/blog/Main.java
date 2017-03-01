package com.cj.blog;


import com.cj.blog.dao.BlogDao;
import com.cj.blog.model.Blogs;
import com.cj.blog.model.BlogEntry;
import com.cj.blog.model.NotFoundException;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;


import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;


public class Main {

    public static BlogDao blogs = new Blogs();
    public static final String FLASH_MESSAGE_KEY = "flash-message";
    private static String password;


    public static void main(String[] args) {
        // uncomment below to add test blog entries
        /*BlogEntry blogEntry = new BlogEntry("First Blog","admin", "I've never written a blog entry before now", null);
        blogs.addEntry(blogEntry);
        BlogEntry blogEntry2 = new BlogEntry("Second Blog", "admin", "This is my second blog entry", null);
        blogs.addEntry(blogEntry2);*/

        staticFileLocation("/public");

        before((req, res) -> {
           if (req.cookie("username") != null && req.cookie("password") != null) {
               req.attribute("username", req.cookie("username"));
               password = req.cookie("password");
               //res.removeCookie("username"); // remove cookie after an attribute is created, so that no one can see username cookie
           }
        });

        before("details/edit/:slug", (req, res) -> {
            String username = req.attribute("username");
            if (username == null || ! username.equals("admin") || password == null) {
                    setFlashMessage(req, "Please log in first");
                    res.redirect("/sign-in");
            }
        });

        before("details/delete/:slug", (req, res) -> {
            String username = req.attribute("username");
            if (username == null || ! username.equals("admin") || password == null) {
                setFlashMessage(req, "Please log in first");
                res.redirect("/sign-in");
            }
        });

        before("/new", (req, res) -> {
            String username = req.attribute("username");
            if (username == null || ! username.equals("admin") || password == null) {
                setFlashMessage(req, "Please log in first");
                res.redirect("/sign-in");
            }
        });

        get("/contact-us", (req, res) -> new ModelAndView(null, "contact-us.hbs"), new HandlebarsTemplateEngine());

        get("/terms", (req, res) -> new ModelAndView(null, "terms.hbs"), new HandlebarsTemplateEngine());

        get("details/edit/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entry", blogs.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("blogEntries", blogs.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("/sign-in", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put(FLASH_MESSAGE_KEY, getFlashMessage(req));
            return new ModelAndView(model, "before-sign-in.hbs");
        }, new HandlebarsTemplateEngine());

        get("/new", (req, res) -> {
            return new ModelAndView(null, "new.hbs");
        }, new HandlebarsTemplateEngine());

        get("/details/:slug", (req, res) -> {
            Map<String, BlogEntry> model = new HashMap<>();
            model.put("entry", blogs.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("details/delete/:slug", (req, res) -> {
            //req.session().attribute("entry-to-delete", req.queryParams("entry"));
            Map<String, Object> model = new HashMap<>();
            model.put("entry", blogs.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "delete.hbs");
        }, new HandlebarsTemplateEngine());

        post("/", (req, res) -> {
            String title = req.queryParams("title");
            String text = req.queryParams("text");
            String[] tags = req.queryParamsValues("tags");
            BlogEntry entry = new BlogEntry(title, "admin", text, tags);
            blogs.addEntry(entry);

            setFlashMessage(req, "Entry Added!");

            Map<String, Object> model = new HashMap<>();
            model.put("blogEntries", blogs.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        post("/details/delete/:slug", (req, res) -> {
            BlogEntry entry = blogs.findEntryBySlug(req.params("slug"));
            blogs.removeBlogEntry(entry);
            setFlashMessage(req, "Entry Deleted!");
            Map<String, Object> model = new HashMap<>();
            model.put(FLASH_MESSAGE_KEY, getFlashMessage(req));
            return new ModelAndView(model, "delete.hbs");
        }, new HandlebarsTemplateEngine());

        post("/details/:slug", (req, res) -> {
            // New Comments
            String name = req.queryParams("name");
            String comment = req.queryParams("comment");
            if (name != null && comment != null) {
                BlogEntry entry = blogs.findEntryBySlug(req.params("slug"));
                entry.addComment(entry, req.queryParams("name"), req.queryParams("comment"));
                BlogEntry entry1 = blogs.findEntryBySlug(req.params("slug"));
                Map<String, Object> model = new HashMap<>();
                model.put("entry", entry1);
                return new ModelAndView(model, "detail.hbs");
            }

            // Edited Entry
            String slug = req.params("slug");
            String title = req.queryParams("title");
            String text = req.queryParams("text");
            blogs.editBlogEntry(slug, title, text); //TODO:  CJ editBlogEntry() returns boolean...test if FALSE

            // after entry is edited, get entry to add to model
            BlogEntry updatedEntry = blogs.findEntryBySlug(slug);
            Map<String, BlogEntry> model = new HashMap<>();
            model.put("entry", updatedEntry);
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        post("/sign-in", (req, res) -> {
            if (! req.queryParams("username").equals("admin")) {
                setFlashMessage(req, "Sorry, that username or password was not correct");
                res.redirect("/sign-in");
                return null;
            } else {
                res.cookie("username", req.queryParams("username"));
                res.cookie("password", req.queryParams("password"));
                Map<String, Object> model = new HashMap<>();
                model.put("blogEntries", blogs.findAllEntries());
                return new ModelAndView(model, "index.hbs");
            }
        }, new HandlebarsTemplateEngine());

        exception(NotFoundException.class, (exc, req, res) ->{
            res.status(404);
            HandlebarsTemplateEngine engine = new HandlebarsTemplateEngine();
            String html = engine.render(new ModelAndView(null, "not-found.hbs"));
            res.body(html);
        });
    }

    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request req) {
        if (req.session(false) == null) { // passing false as the argument returns null if there is no current session per docs
            return null;
        }
        if (! req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        String message = req.session().attribute(FLASH_MESSAGE_KEY);
        req.session().removeAttribute(FLASH_MESSAGE_KEY);
        return message;
    }
}
