package service;

import annotation.Service;
import data.Student;

@Service(StudentService.class)
public class StudentServiceImpl implements StudentService{

    @Override
    public void getInfo(Student student) {
        System.out.println(student);
    }
}
