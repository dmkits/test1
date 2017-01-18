package core;

/**
 * Created by dmkits on 11.03.16.
 */

import java.io.*;
import java.util.Properties;

/** AppLocalConfig
 * Локальные настройки приложения.
 * По умолчанию локальные настройки хранятся в папке приложения в файле .properties.
 * Содержит методы чтения настроек из файла, записи настроек в файл,
 *  метод записи значений и получения значений настроек по ключу,
 *  метод, устанавливающий настройки по умолчанию, метод удаления файла настроек.
 * @author dmkits
 * @version 1.0 (2016-03-11)
 */
public class AppLocalConfig {

    private Properties m_propsconfig;
    private String filename;
    private File configfile;

    public AppLocalConfig() { init(getDefaultConfigFile()); }
    public AppLocalConfig(String sconfigfilename) {
        if (sconfigfilename.equals("")) { init(getDefaultConfigFile());
        } else { init(new File(filename=sconfigfilename)); }
    }
    public AppLocalConfig(File configfile) { init(configfile); }
    private void init(File configfile) { this.configfile = configfile; m_propsconfig = new Properties(); }

    private File getDefaultConfigFile() {
        String dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;
        return new File(new File(dirname), filename="application.properties");
    }
    public File getConfigFile() { return configfile; }

    public Properties getProperties() { return m_propsconfig; }
    public String getProperty(String sKey) { return m_propsconfig.getProperty(sKey); }
    public void setProperty(String sKey, String sValue) {
        if (sValue == null) { m_propsconfig.remove(sKey); } else { m_propsconfig.setProperty(sKey, sValue); }
    }

    public void setDefault() {
        String dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;
//        m_propsconfig.setProperty("", ""); //
    }

    public void load() throws IOException {
        InputStream in = new FileInputStream(configfile);
        if (in != null) { m_propsconfig.load(in); in.close(); }
    }
    public void save() throws IOException {
        OutputStream out = new FileOutputStream(configfile);
        if (out != null) { m_propsconfig.store(out, "Application configuration file."); out.close(); }
    }
    public boolean delete() { setDefault(); return configfile.delete(); }
}
