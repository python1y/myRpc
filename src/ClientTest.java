import data.Student;
import proxy.RpcClientProxy;
import service.StudentService;

public class ClientTest {

    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9090);
        StudentService service = proxy.getProxy(StudentService.class);
        Student student = new Student();
        student.setAge(1);
        student.setName("213");
        service.getInfo(student);
    }
}
