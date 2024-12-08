package com.iteaj.iboot.plugin.protocol.opcua.network;

import lombok.AllArgsConstructor;
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

    public NodeWrapper(final String address, final Object value) {
        this.nodeId = NodeId.parse(address);
        this.value = new DataValue(new Variant(value));
    }

    public NodeWrapper(NodeId nodeId, DataValue value) {
        this.nodeId = nodeId;
        this.value = value;
    }
}
