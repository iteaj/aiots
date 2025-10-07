package com.iteaj.iboot.plugin.protocol.opcua.network;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

@Getter
@Setter
public class NodeWrapper {

    /**
     * 节点
     */
    private NodeId nodeId;

    /**
     * 值
     */
    private DataValue value;

    protected NodeWrapper(final String address) {
        this.nodeId = NodeId.parse(address);
    }

    protected NodeWrapper(final String address, final Object value) {
        this(NodeId.parse(address), new Variant(value));
    }

    public NodeWrapper(NodeId nodeId, Variant value) {
        this.nodeId = nodeId;
        this.value = DataValue.newValue().setValue(value).build();
    }

    public static NodeWrapper readBuild(final String address) {
        return new NodeWrapper(address);
    }

    public static NodeWrapper writeBuild(final String address, final Object value) {
        return new NodeWrapper(address, value);
    }
}
