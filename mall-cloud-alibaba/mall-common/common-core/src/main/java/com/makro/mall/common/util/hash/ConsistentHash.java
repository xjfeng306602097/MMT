package com.makro.mall.common.util.hash;

import java.util.Set;
import java.util.TreeMap;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/8/2
 */
public class ConsistentHash<T> {

    private static final int DEFAULT_VIRTUAL_COUNT = 5;

    private int virtualCount = DEFAULT_VIRTUAL_COUNT;

    /**
     * 使用的hash算法
     */
    private HashAlgorithm alg = HashAlgorithm.KETAMA_HASH;

    /**
     * 节点列表
     */
    private Set<T> nodeSet;

    /**
     * hash,Node
     */
    private final TreeMap<Long, T> nodeMap = new TreeMap<Long, T>();

    public ConsistentHash() {
    }

    public void setNodeSet(Set<T> nodeList) {
        this.nodeSet = nodeList;
    }

    /**
     * 获取环形HASH
     *
     * @return
     */
    public TreeMap<Long, T> getNodeMap() {
        return nodeMap;
    }

    /**
     * 设置一致性HASH的算法，默认采用 KETAMA_HASH
     * 对于一致性HASH而言选择的HASH算法首先要考虑发散度其次再考虑性能
     *
     * @param alg 具体支持的算法
     * @see HashAlgorithm
     */
    public void setAlg(HashAlgorithm alg) {
        this.alg = alg;
    }

    /**
     * 设置每个节点的虚拟节点个数，该参数默认是100
     *
     * @param virtualCount 虚拟节点数
     */
    public void setVirtualCount(int virtualCount) {
        this.virtualCount = virtualCount;
    }

    /**
     * 构建一致性HASH环
     */
    public void buildHashCircle() {
        if (nodeSet == null) {
            return;
        }
        for (T node : nodeSet) {
            for (int i = 0; i < virtualCount; i++) {
                long nodeKey = this.alg.hash(node.toString() + "-" + i);
                nodeMap.put(nodeKey, node);
            }
        }
    }

    /**
     * 沿环的顺时针找到虚拟节点
     *
     * @param key
     * @return
     */
    public T findNodeByKey(String key) {
        final Long hash = this.alg.hash(key);
        Long target = hash;
        if (!nodeMap.containsKey(hash)) {
            target = nodeMap.ceilingKey(hash);
            if (target == null && !nodeMap.isEmpty()) {
                target = nodeMap.firstKey();
            }
        }
        return nodeMap.get(target);
    }


}
