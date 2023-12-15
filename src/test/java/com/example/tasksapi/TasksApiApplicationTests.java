package com.example.tasksapi;

import com.example.tasksapi.controllers.TaskController;
import com.example.tasksapi.models.Category;
import com.example.tasksapi.models.Task;
import com.example.tasksapi.repositories.CategoryRepository;
import com.example.tasksapi.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TasksApiApplicationTests {

    @Autowired
    private TaskController taskController;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final List<Task> tasks = new ArrayList<>();
    private final Set<Category> categories = new HashSet<>();

    private static String long_value = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                        "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
                        "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                        "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
                        "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    @BeforeAll
    void setUp(){
        Task task1 = new Task("Test_name","Test_description");
        Task task2 = new Task("o","a");
        Task task3 = new Task("long_description",long_value);
        Task taskNCategories = new Task("Title", "Description");
        Category category1 = new Category("test1");
        Category category2 = new Category("test2");
        categories.add(category1);
        categories.add(category2);
        taskNCategories.setCategories(categories);
        categoryRepository.saveAll(categories);
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(taskNCategories);
        taskRepository.saveAll(tasks);

    }

    @Test
    void testGetAllTasksWithoutFiltering(){
        List<Task> taskList = taskController.getAllTasks("asc","id","","");

        assertEquals(taskList.size(), tasks.size());
    }

    @Test
    void testGetAllTasksWithFilteringByTitle(){
        List<Task> taskList = taskController.getAllTasks("asc","id","Test_name","");

        assertEquals(taskList.get(0).getId(), tasks.get(0).getId());
        assertEquals(taskList.size(),1);
    }

    @Test
    void testGetAllTasksWithFilteringByTitleAndDescription(){
        List<Task> taskList = taskController.getAllTasks("asc","id","Test_name","Test_description");

        assertEquals(taskList.get(0).getId(), tasks.get(0).getId());
        assertEquals(taskList.size(),1);
    }
    @Test
    void testGetAllTasksWithFilteringByTitleAndDescriptionNoResult(){
        List<Task> taskList = taskController.getAllTasks("asc","id","Test_name","a");

        assertEquals(taskList.size(),0);
    }

    @Test
    void testGetTaskById(){
        Long id = tasks.get(0).getId();
        Optional<Task> task = taskController.getById(id);

        assertTrue(task.isPresent());
        assertEquals(task.get().getId(),tasks.get(0).getId());
    }

    @Test
    void testGetTaskByIdNoExists(){
        Long id = 99999L;
        Optional<Task> task = taskController.getById(id);

        assertFalse(task.isPresent());
    }

    @Test
    void createNewTask(){
        List<String> new_categories = new LinkedList<>();
        new_categories.add("testcat");
        taskController.createTask("test", "test", new_categories, null);
        List<Task> allTasks = taskRepository.findAll();
        List<Task> taskFromDB = taskRepository.findTasksByField("test", "test");

        assertEquals(allTasks.size(),tasks.size() + 1);
        assertEquals(taskFromDB.size(), 1);
        assertEquals(categories.size() + 1, categoryRepository.findAll().size());

        taskRepository.delete(taskFromDB.remove(0));

    }

    @Test
    void createNewBadTask(){
        List<String> new_categories = new LinkedList<>();
        new_categories.add("testcat");
        taskController.createTask(long_value, "", new_categories, null);
        List<Task> allTasks = taskRepository.findAll();

        assertEquals(allTasks.size(),tasks.size());
    }

    @Test
    void updateOneField(){
        String old_title = tasks.get(0).getTitle();
        taskController.updateById(tasks.get(0).getId(),"new_title", null);

        assertNotEquals(old_title, taskRepository.findById(tasks.get(0).getId()).get().getTitle());
    }

    @Test
    void updateAllFields(){
        String old_title = tasks.get(0).getTitle();
        String old_descr = tasks.get(0).getDescription();
        taskController.updateById(tasks.get(0).getId(),"new_b", "new_c");

        assertNotEquals(old_title, taskRepository.findById(tasks.get(0).getId()).get().getTitle());
        assertNotEquals(old_descr, taskRepository.findById(tasks.get(0).getId()).get().getTitle());
    }

    @Test
    void deleteByIdSuccess(){
        taskController.deleteById(tasks.get(0).getId());

        assertEquals(taskRepository.findAll().size(),tasks.size() - 1);
        tasks.remove(0);
    }

    @Test
    void deleteByIdBad(){
        taskController.deleteById(999999999L);

        assertEquals(taskRepository.findAll().size(),tasks.size());
    }
}
