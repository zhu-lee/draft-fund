package lee.fund.remote.exception;

import lee.fund.remote.netty.client.ClientConfig;
import lee.fund.util.config.ConfigProperties;
import lee.fund.util.lang.FaultException;
import org.apache.commons.text.TextStringBuilder;

import java.util.List;

public class RpcExceptions {
    private static final String SERVER_NAME = "serverName";
    private static final String SERVER_ADDRESS = "serverAddress";
    private static final String REMOTE_ERROR = "remoteError";
    private static final String NODE_ERRORS = "nodeErrors";
    private static final String PROFILE = "profile";

    private RpcExceptions() {
    }

    public static void putRemoteError(FaultException fault, String remoteException) {
        fault.getData().put(REMOTE_ERROR, remoteException);
    }

    public static void putRemoteServer(FaultException fault, ClientConfig clientConfig) {
        fault.getData().put(SERVER_NAME, clientConfig.getName());
        fault.getData().put(SERVER_ADDRESS, clientConfig.getAddress().toString());
    }

    public static void putNodeFaults(FaultException fault, List<FaultException> faultExceptions) {
        TextStringBuilder sb = new TextStringBuilder();
        sb.appendNewLine();
        for (FaultException e : faultExceptions) {
            sb.appendln(e.toStringDetail());
        }
        fault.getData().put(NODE_ERRORS, sb.toString());
    }

    public static void putProfile(FaultException fault) {
        fault.getData().put(PROFILE, ConfigProperties.INSTANCE.getActiveProfile());
    }
}
