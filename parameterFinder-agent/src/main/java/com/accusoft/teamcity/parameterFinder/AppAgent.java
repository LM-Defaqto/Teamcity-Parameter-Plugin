package com.accusoft.teamcity.parameterFinder;

import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

public class AppAgent extends AgentLifeCycleAdapter {

    private StringBuilder s = null;
    Map<String, String> values = new HashMap<String, String>();

    public AppAgent(@NotNull EventDispatcher<AgentLifeCycleListener> dispatcher) {
        s = new StringBuilder();
        dispatcher.addListener(this);
    }

    @Override
    public void agentInitialized(@NotNull final BuildAgent agent) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(getClass().getResourceAsStream("/buildAgentResources/parameters.xml"));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("parameter");

            ArrayList<String> locations = new ArrayList<String>();

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                locations.clear();

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String toolName = (eElement.getElementsByTagName("tool").item(0).getTextContent());
                    NodeList list = eElement.getElementsByTagName("location");

                    for (int j = 0; j < list.getLength(); j++) {
                        String location = list.item(j).getTextContent();

                        if (location != null && location.compareTo("") != 0 && location.compareTo(" ") != 0 && location.compareTo("null") != 0) {
                            location = location.replaceAll("[/\\\\]+", Matcher.quoteReplacement(System.getProperty("file.separator")));
                        }

                        locations.add(location);
                    }

			        NodeList files = eElement.getElementsByTagName("file");
                    Node fileNode = files == null ? null : files.item(0);
                    String file = fileNode == null ? null : fileNode.getTextContent();
                    String command = eElement.getElementsByTagName("command").item(0).getTextContent();
                    String regex = eElement.getElementsByTagName("regex").item(0).getTextContent();

                    new ParameterFinder(toolName, regex, locations, command, file, this);
                }
            }
        } catch (Exception e) {
            buildLogString(e.toString());
        } finally {
            log(s);
        }

        BuildAgentConfiguration conf = agent.getConfiguration();
        Iterator it = values.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            conf.addEnvironmentVariable(pair.getKey().toString(), pair.getValue().toString());
            it.remove();
        }
    }

    public void log(StringBuilder s) {
        Loggers.AGENT.info(s.toString());
    }

    public void buildLogString(String s) {
        this.s.append(s);
    }
}
