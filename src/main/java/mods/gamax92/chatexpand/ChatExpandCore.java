package mods.gamax92.chatexpand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(value = "ChatExpand CoreMod")
@IFMLLoadingPlugin.SortingIndex(value = Integer.MAX_VALUE)
public class ChatExpandCore implements IFMLLoadingPlugin {
	protected static final Logger logger = LogManager.getLogger("ChatExpand");
	private static final Matcher cfgmatch = Pattern.compile("^ChatLength\\s*=\\s*(\\d+)$").matcher("");

	public static int CHAT_LENGTH = 256;

	private void saveConfig(File configLocation) {
		logger.log(Level.INFO, "Saving configuration ...");

		configLocation.getParentFile().mkdir();
		PrintWriter cfgOut = null;
		try {
			cfgOut = new PrintWriter(new FileWriter(configLocation));
			cfgOut.println("# Empty lines or lines starting with a '#' are ignored");
			cfgOut.println();
			cfgOut.println("# Chat Length, must be above or equal to 100");
			cfgOut.println("ChatLength=" + CHAT_LENGTH);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (cfgOut != null) {
				cfgOut.close();
			}
		}
	}

	private void loadConfig(File configLocation) {
		BufferedReader cfgIn = null;
		try {
			cfgIn = new BufferedReader(new FileReader(configLocation));
			String text = null;

			logger.log(Level.INFO, "Loading configuration ...");

			while ((text = cfgIn.readLine()) != null) {
				text = text.trim();
				if (!text.equals("") && !text.startsWith("#")) {
					cfgmatch.reset(text);
					if (cfgmatch.find())
						CHAT_LENGTH = Integer.parseUnsignedInt(cfgmatch.group(1), 10);
				}
			}
			if (CHAT_LENGTH < 100) {
				logger.log(Level.ERROR, "Chat length cannot be set less than 100, using defaults");
				CHAT_LENGTH = 256;
			}
		} catch (FileNotFoundException e) {
			logger.log(Level.ERROR, configLocation.getName() + " missing, using defaults");
			CHAT_LENGTH = 256;
		} catch (IOException e) {
			e.printStackTrace();
			CHAT_LENGTH = 256;
		} finally {
			try {
				if (cfgIn != null) {
					cfgIn.close();
				}
			} catch (IOException e) {
			}
		}
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { ChatExpandTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return ChatExpandModContainer.class.getName();
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		if (data.containsKey("mcLocation")) {
			File configLocation = new File((File) data.get("mcLocation"), "config/ChatExpand.cfg");
			loadConfig(configLocation);
			saveConfig(configLocation);
		} else {
			logger.log(Level.ERROR, "mcLocation key missing, using defaults");
			CHAT_LENGTH = 256;
		}
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}