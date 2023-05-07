package com.dzhatdoev.todo.controller;

import com.dzhatdoev.entities.entity.Category;
import com.dzhatdoev.entities.entity.Priority;
import com.dzhatdoev.todo.search.CategorySearchValues;
import com.dzhatdoev.todo.search.PrioritySearchValues;
import com.dzhatdoev.todo.service.CategoryService;
import com.dzhatdoev.todo.service.PriorityService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


/*

Используем @RestController вместо обычного @Controller, чтобы все ответы сразу оборачивались в JSON,
иначе пришлось бы добавлять лишние объекты в код, использовать @ResponseBody для ответа, указывать тип отправки JSON

Названия методов могут быть любыми, главное не дублировать их имена внутри класса и URL mapping

*/

@RestController
@RequestMapping("/category") // базовый URI
public class CategoryController {

    // доступ к данным из БД
    private CategoryService categoryService;

    // используем автоматическое внедрение экземпляра класса через конструктор
    // не используем @Autowired ля переменной класса, т.к. "Field injection is not recommended "
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/test")
    public ResponseEntity<?> getCategory() {
        return ResponseEntity.ok("hello");
    }

    @PostMapping("/all")
    public List<Category> findAll(@RequestBody Long id) {
        return categoryService.findAll(id);
    }


    @PostMapping("/add")
    public ResponseEntity<Category> add(@RequestBody Category category) {

        // проверка на обязательные параметры
        if (category.getId() != null && category.getId() != 0) { // это означает, что id заполнено
            // id создается автоматически в БД (autoincrement), поэтому его передавать не нужно, иначе может быть конфликт уникальности значения
            return new ResponseEntity("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (category.getTitle() == null || category.getTitle().trim().length() == 0) {
            return new ResponseEntity("missed param: title MUST be not null", HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(categoryService.add(category)); // возвращаем добавленный объект с заполненным ID
    }



    @PutMapping("/update")
    public ResponseEntity update(@RequestBody Category category) {

        // проверка на обязательные параметры
        if (category.getId() == null || category.getId() == 0) {
            return new ResponseEntity("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (category.getTitle() == null || category.getTitle().trim().length() == 0) {
            return new ResponseEntity("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        // save работает как на добавление, так и на обновление
        categoryService.update(category);

        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)
    }



    // для удаления используем тип запроса DELETE и передаем ID для удаления
    // можно также использовать метод POST и передавать ID в теле запроса
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {

        // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
        // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
        try {
            categoryService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 без объектов (операция прошла успешно)
    }


    // поиск по любым параметрам CategorySearchValues
    @PostMapping("/search")
    public ResponseEntity<List<Category>> search(@RequestBody CategorySearchValues categorySearchValues) {

        // проверка на обязательные параметры
        if (categorySearchValues.getUserId() == null || categorySearchValues.getUserId() == 0) {
            return new ResponseEntity("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        // поиск категорий пользователя по названию
        List<Category> list = categoryService.findByTitle(categorySearchValues.getTitle(), categorySearchValues.getUserId());

        return ResponseEntity.ok(list);
    }


    // параметр id передаются не в BODY запроса, а в самом URL
    @PostMapping("/id")
    public ResponseEntity<Category> findById(@RequestBody Long id) {

        Category category = null;

        // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
        // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
        try {
            category = categoryService.findById(id);
        } catch (NoSuchElementException e) { // если объект не будет найден
            e.printStackTrace();
            return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(category);
    }

    @RestController
    @RequestMapping("/priority") // базовый URI
    public static class PriorityController {

        // доступ к данным из БД
        private PriorityService priorityService;

        // используем автоматическое внедрение экземпляра класса через конструктор
        // не используем @Autowired ля переменной класса, т.к. "Field injection is not recommended "
        public PriorityController(PriorityService priorityService) {
            this.priorityService = priorityService;
        }


        @PostMapping("/all")
        public List<Priority> findAll(@RequestBody Long id) {
            return priorityService.findAll(id);
        }


        @PostMapping("/add")
        public ResponseEntity<Priority> add(@RequestBody Priority priority) {

            // проверка на обязательные параметры
            if (priority.getId() != null && priority.getId() != 0) {
                // id создается автоматически в БД (autoincrement), поэтому его передавать не нужно, иначе может быть конфликт уникальности значения
                return new ResponseEntity("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
            }

            // если передали пустое значение title
            if (priority.getTitle() == null || priority.getTitle().trim().length() == 0) {
                return new ResponseEntity("missed param: title", HttpStatus.NOT_ACCEPTABLE);
            }

            // если передали пустое значение color
            if (priority.getColor() == null || priority.getColor().trim().length() == 0) {
                return new ResponseEntity("missed param: color", HttpStatus.NOT_ACCEPTABLE);
            }

            // save работает как на добавление, так и на обновление
            return ResponseEntity.ok(priorityService.add(priority));
        }


        @PutMapping("/update")
        public ResponseEntity update(@RequestBody Priority priority) {

            // проверка на обязательные параметры
            if (priority.getId() == null || priority.getId() == 0) {
                return new ResponseEntity("missed param: id", HttpStatus.NOT_ACCEPTABLE);
            }

            // если передали пустое значение title
            if (priority.getTitle() == null || priority.getTitle().trim().length() == 0) {
                return new ResponseEntity("missed param: title", HttpStatus.NOT_ACCEPTABLE);
            }

            // если передали пустое значение color
            if (priority.getColor() == null || priority.getColor().trim().length() == 0) {
                return new ResponseEntity("missed param: color", HttpStatus.NOT_ACCEPTABLE);
            }

            // save работает как на добавление, так и на обновление
            priorityService.update(priority);


            return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)

        }

        // параметр id передаются не в BODY запроса, а в самом URL
        @PostMapping("/id")
        public ResponseEntity<Priority> findById(@RequestBody Long id) {

            Priority priority = null;

            // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
            // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
            try {
                priority = priorityService.findById(id);
            } catch (NoSuchElementException e) { // если объект не будет найден
                e.printStackTrace();
                return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
            }

            return ResponseEntity.ok(priority);
        }


        // для удаления используем типа запроса put, а не delete, т.к. он позволяет передавать значение в body, а не в адресной строке
        @DeleteMapping("/delete/{id}")
        public ResponseEntity delete(@PathVariable("id") Long id) {

            // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
            // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
            try {
                priorityService.deleteById(id);
            } catch (EmptyResultDataAccessException e) {
                e.printStackTrace();
                return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
            }

            return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)
        }


        // поиск по любым параметрам PrioritySearchValues
        @PostMapping("/search")
        public ResponseEntity<List<Priority>> search(@RequestBody PrioritySearchValues prioritySearchValues) {

            // проверка на обязательные параметры
            if (prioritySearchValues.getUserId() == null || prioritySearchValues.getUserId() == 0) {
                return new ResponseEntity("missed param: email", HttpStatus.NOT_ACCEPTABLE);
            }

            // если вместо текста будет пусто или null - вернутся все категории
            return ResponseEntity.ok(priorityService.find(prioritySearchValues.getTitle(), prioritySearchValues.getUserId()));
        }


    }
}
