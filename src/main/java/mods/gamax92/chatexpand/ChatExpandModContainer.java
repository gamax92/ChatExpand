package mods.gamax92.chatexpand;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class ChatExpandModContainer extends DummyModContainer {
	public ChatExpandModContainer() {
		super(new ModMetadata());

		ModMetadata metadata = super.getMetadata();
		metadata.authorList = Arrays.asList(new String[] { "gamax92" });
		metadata.description = "Allows the chat length limit to be configured.";
		metadata.modId = "chatexpand";
		metadata.version = "1.0.0";
		metadata.name = "ChatExpand";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		return true;
	}
}
