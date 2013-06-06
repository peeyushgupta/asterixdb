package edu.uci.ics.asterix.common.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import edu.uci.ics.asterix.common.configuration.AsterixConfiguration;
import edu.uci.ics.asterix.common.configuration.Property;
import edu.uci.ics.asterix.common.configuration.Store;
import edu.uci.ics.asterix.common.exceptions.AsterixException;

public class AsterixPropertiesAccessor {
    private static final Logger LOGGER = Logger.getLogger(AsterixPropertiesAccessor.class.getName());

    private final String instanceName;
    private final String metadataNodeName;
    private final Set<String> nodeNames;
    private final Map<String, String[]> stores;
    private final Map<String, Property> asterixConfigurationParams;

    public AsterixPropertiesAccessor() throws AsterixException {
        String fileName = System.getProperty(GlobalConfig.CONFIG_FILE_PROPERTY);
        if (fileName == null) {
            fileName = GlobalConfig.DEFAULT_CONFIG_FILE_NAME;
        }
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            try {
                fileName = GlobalConfig.DEFAULT_CONFIG_FILE_NAME;
                is = new FileInputStream(fileName);
            } catch (FileNotFoundException fnf) {
                throw new AsterixException("Could not find configuration file " + fileName);
            }
        }

        AsterixConfiguration asterixConfiguration = null;
        try {
            JAXBContext ctx = JAXBContext.newInstance(AsterixConfiguration.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            asterixConfiguration = (AsterixConfiguration) unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            throw new AsterixException("Failed to read configuration file " + fileName);
        }
        instanceName = asterixConfiguration.getInstanceName();
        metadataNodeName = asterixConfiguration.getMetadataNode();
        stores = new HashMap<String, String[]>();
        List<Store> configuredStores = asterixConfiguration.getStore();
        nodeNames = new HashSet<String>();
        for (Store store : configuredStores) {
            String trimmedStoreDirs = store.getStoreDirs().trim();
            stores.put(store.getNcId(), trimmedStoreDirs.split(","));
            nodeNames.add(store.getNcId());
        }
        asterixConfigurationParams = new HashMap<String, Property>();
        for (Property p : asterixConfiguration.getProperty()) {
            asterixConfigurationParams.put(p.getName(), p);
        }
    }

    public String getMetadataNodeName() {
        return metadataNodeName;
    }

    public String getMetadataStore() {
        return stores.get(metadataNodeName)[0];
    }

    public Map<String, String[]> getStores() {
        return stores;
    }

    public Set<String> getNodeNames() {
        return nodeNames;
    }

    public <T> T getProperty(String property, T defaultValue, IPropertyInterpreter<T> interpreter) {
        Property p = asterixConfigurationParams.get(property);
        if (p == null) {
            return defaultValue;
        }

        try {
            return interpreter.interpret(p);
        } catch (IllegalArgumentException e) {
            logConfigurationError(p, defaultValue);
            throw e;
        }
    }

    private <T> void logConfigurationError(Property p, T defaultValue) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
            LOGGER.severe("Invalid property value '" + p.getValue() + "' for property '" + p.getName()
                    + "'.\n See the description: \n" + p.getDescription() + "\nDefault = " + defaultValue);
        }
    }

    public String getInstanceName() {
        return instanceName;
    }
}