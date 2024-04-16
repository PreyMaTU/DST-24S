package dst.ass2.proto.auth;

import io.grpc.MethodDescriptor;
import io.grpc.ServiceDescriptor;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProtoSpecificationTest {

    private ClassLoader cl;
    public static final String SERVICE_NAME = "dst.ass2.service.api.auth.proto.AuthService";
    public static final String GRPC_CLASS_NAME = SERVICE_NAME + "Grpc";

    @Before
    public void setUp() throws Exception {
        cl = ProtoSpecificationTest.class.getClassLoader();
    }

    @Test
    public void generatedClass_exists() throws Exception {
        try {
            cl.loadClass(GRPC_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("Classpath did not contain expected gRPC service class", e);
        }
    }

    @Test
    public void generatedClass_hasMethods() throws Exception {
        assertThat(getMethodDescriptors().keySet(), hasItems(
            SERVICE_NAME + "/authenticate",
            SERVICE_NAME + "/validateToken"
        ));
    }

    private Map<String, MethodDescriptor> getMethodDescriptors() throws ClassNotFoundException {
        return getMethodDescriptors(getServiceDescriptor(cl.loadClass(GRPC_CLASS_NAME)));
    }

    private Map<String, MethodDescriptor> getMethodDescriptors(ServiceDescriptor sd) {
        return sd.getMethods().stream()
            .collect(Collectors.toMap(
                MethodDescriptor::getFullMethodName,
                Function.identity()
            ));
    }

    private ServiceDescriptor getServiceDescriptor(Class<?> grpcClass) {
        try {
            Method getServiceDescriptor = grpcClass.getDeclaredMethod("getServiceDescriptor");
            getServiceDescriptor.setAccessible(true);
            return (ServiceDescriptor) getServiceDescriptor.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Error finding service descriptor in " + grpcClass.getName(), e);
        }
    }
}
