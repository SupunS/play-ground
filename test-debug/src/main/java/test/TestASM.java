package test;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestASM {

    private static final String SOURCE_FILE_NAME = "ballerina/test.bal";
    private static final String BAL_SOURCE_INTERFACE = "test/BalSource";
    private static final String CLASS_NAME = "test/BazImpl";
    private static final String VAR_1 = "a";
    private static final String VAR_2 = "b";
    private static final String IO_PRINTLN = "org/ballerinalang/stdlib/io/nativeimpl/PrintlnAny";

    public static void main(String[] args) throws Exception {
        /*
         * ballerina source:
         * -----------------
         * 
         * function main(int a, int b) returns int {
         *     return bar(a, b);
         * }
         * 
         * public bar(int a, int b) returns int {
         *     io:println(a);
         *     io:println(b);
         *     a = a + b;
         *     panic error();
         *     ...
         * }
         * 
         */

        byte[] bytes = generateClass();
        DynamicClassLoader loader = new DynamicClassLoader();
        Class<?> clazz = loader.defineClass("test.BazImpl", bytes);
        BalSource balFile = (BalSource) clazz.newInstance();

        // Class<?> clazz = TestASM.class.getClassLoader().loadClass(CLASS_NAME);
        // BalSource calc = (BalSource) clazz.newInstance();

        // Run the main function
        int a = 2, b = 5;
        balFile.main(a, b);
    }

    private static byte[] generateClass() {
        /*
         * Generated output (.class):
         * --------------------------
         * 
         * public int main(int a, int b) {
         *     bar(a, b);
         * }
         * 
         * public int bar(int a, int b) {
         *     io:println();
         *     a = a + b;
         *     throw new Exception();
         *     ...
         * }
         * 
         */

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, CLASS_NAME, null, "java/lang/Object",
                new String[] { BAL_SOURCE_INTERFACE });
        cw.visitSource(SOURCE_FILE_NAME, null);


        // Constructor
        MethodVisitor con = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        con.visitCode();
        addLineNumber(con, 1);
        con.visitVarInsn(Opcodes.ALOAD, 0);
        con.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        con.visitInsn(Opcodes.RETURN);
        con.visitMaxs(1, 1);

        // bar() function definition
        {
            MethodVisitor mv2 = cw.visitMethod(Opcodes.ACC_PUBLIC, "bar", "(II)I", null, null);
            mv2.visitCode();

            // Print to std out
            mv2.visitVarInsn(Opcodes.ILOAD, 1);
            mv2.visitMethodInsn(Opcodes.INVOKESTATIC, IO_PRINTLN, "exec", "(I)V", false);
            mv2.visitVarInsn(Opcodes.ILOAD, 2);
            mv2.visitMethodInsn(Opcodes.INVOKESTATIC, IO_PRINTLN, "exec", "(I)V", false);

            // var1 = var1 + var2
            Label l0 = addLineNumber(mv2, 4);
            mv2.visitVarInsn(Opcodes.ILOAD, 1);
            mv2.visitVarInsn(Opcodes.ILOAD, 2);
            mv2.visitInsn(Opcodes.IADD);
            mv2.visitVarInsn(Opcodes.ISTORE, 1);

            // Throw an exception
            addLineNumber(mv2, 12);
            String exClass = Type.getInternalName(Exception.class);
            mv2.visitTypeInsn(Opcodes.NEW, exClass);
            mv2.visitInsn(Opcodes.DUP);
            mv2.visitMethodInsn(Opcodes.INVOKESPECIAL, exClass, "<init>", "()V", false);
            mv2.visitInsn(Opcodes.ATHROW);

            Label l2 = addLineNumber(mv2, 15);
            mv2.visitInsn(Opcodes.IRETURN);
            mv2.visitMaxs(-1, -1);

            // Add local variable names
            mv2.visitLocalVariable("this", "L" + CLASS_NAME + ";", null, l0, l2, 0);
            mv2.visitLocalVariable(VAR_1, "I", null, l0, l2, 1);
            mv2.visitLocalVariable(VAR_2, "I", null, l0, l2, 2);

            mv2.visitEnd();
        }

        // foo() function definition
        {
            MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "main", "(II)I", null, null);
            mv.visitCode();

            // Invoke the innerAdd() method
            Label l0 = addLineNumber(mv, 4);
            mv.visitVarInsn(Opcodes.ALOAD, 0);          // Load "this" onto the stack
            mv.visitVarInsn(Opcodes.ILOAD, 1);          // Load int value onto stack
            mv.visitVarInsn(Opcodes.ILOAD, 2);          // Load int value onto stack
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "test/BazImpl", "bar", "(II)I", false);

            Label l1 = addLineNumber(mv, 5);            // Add line number information
            mv.visitInsn(Opcodes.IRETURN);              // Return integer from top of stack
            mv.visitMaxs(-1, -1);                         // Specify max stack and local vars

            // Add local var names
            mv.visitLocalVariable("this", "Ltest/BazImpl;", null, l0, l1, 0);
            mv.visitLocalVariable(VAR_1, "I", null, l0, l1, 1);
            mv.visitLocalVariable(VAR_2, "I", null, l0, l1, 2);

            mv.visitEnd();
        }

        // Finish the class definition
        cw.visitEnd();

        byte[] bytes = cw.toByteArray();
        writeClazz("aaa.class", bytes);
        return bytes;
    }

    private static void writeClazz(String name, byte[] bytes) {
        String filePath = "." + File.separator + name;
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Label addLineNumber(MethodVisitor mv, int line) {
        Label l = new Label();
        mv.visitLabel(l);
        mv.visitLineNumber(line, l);
        return l;
    }
}
