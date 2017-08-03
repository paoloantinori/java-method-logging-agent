package com.redhat.fuse;

import java.lang.instrument.Instrumentation;

/**
 * Hello world!
 *
 */
public class AgentMain {
    public static void premain(String args, Instrumentation inst)
            throws Exception {
        System.out.println("Loading Agent..");
//        inst.addTransformer(new MethodLoggingTransformer());
        inst.addTransformer(new JavassistMethodLoggingTransformer());

    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        premain(args, inst);
    }

    private static boolean isInternal(Class<?> klass) {

        return false;
    }

    private static void processOptions(String options) {
    }
}
