package com.example.socialnetworkgui.service;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GraphUtils {
    private List<Long> biggestComponent;
    private Integer connectedComponents;

    public GraphUtils() { }

    /**
     * Search for all connected components (with BFS) and set the number of components and the biggest one
     * @param nodesRelations the adjacency lists of the graph
     */
    public void setConnectedComponents(@NotNull Map<Long, List<Long>> nodesRelations){
        Map<Long, Boolean> discoveredNodes = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : nodesRelations.entrySet()) {
            Long id = entry.getKey();
            discoveredNodes.put(id,false);
        }
        int components = 0;
        int maxNodes = -1;

        while(true){
            // find an undiscovered node
            Long source = null;
            for (Map.Entry<Long, Boolean> entry : discoveredNodes.entrySet()) {
                Long id = entry.getKey();
                if(!discoveredNodes.get(id)){
                    source = id;
                    break;
                }
            }
            // discovered all nodes so we exit
            if(source == null){
                break;
            }
            // Initialize the list containing the nodes of this component
            int nodes = 0;
            List<Long> componentNodes = new ArrayList<>();
            // BFS
            Queue<Long> queue = new LinkedList<>();
            queue.add(source);
            while (!queue.isEmpty()) {
                Long id = queue.remove();
                List<Long> neighboursIds = nodesRelations.get(id);
                for(Long neighbourId : neighboursIds){
                    if(!discoveredNodes.get(neighbourId)){
                        queue.add(neighbourId);
                    }
                }
                discoveredNodes.remove(id);
                discoveredNodes.put(id, true);
                nodes++;
                componentNodes.add(id);
            }
            // new component
            components++;
            // check max nodes
            if (nodes > maxNodes){
                maxNodes = nodes;
                biggestComponent = componentNodes;
            }
        }
        connectedComponents = components;
    }

    /**
     * @return the biggest connected component (all the nodes)
     */
    public List<Long> getBiggestComponent(){
        return biggestComponent;
    }

    /**
     * @return the number of connected components
     */
    public Integer getConnectedComponents() {
        return connectedComponents;
    }
}

