package fr.skytasul.quests.utils.logger;

import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import fr.skytasul.quests.utils.Lang;

public class LoggerExpanded {
	
	private Logger logger;
	
	public LoggerExpanded(Logger logger) {
		this.logger = logger;
	}
	
	public void info(String msg) {
		logger.info(msg);
	}
	
	public void warning(String msg) {
		logger.log(Level.WARNING, msg);
	}
	
	public void warning(String msg, Throwable throwable) {
		logger.log(Level.WARNING, msg, throwable);
	}
	
	public void severe(String msg) {
		logger.log(Level.SEVERE, msg);
	}
	
	public void severe(String msg, Throwable throwable) {
		logger.log(Level.SEVERE, msg, throwable);
	}

	public <T> BiConsumer<T, Throwable> logError(Consumer<T> consumer, String friendlyErrorMessage, CommandSender sender) {
		return (object, ex) -> {
			if (ex == null) {
				if (consumer != null)
					consumer.accept(object);
			} else {
				if (ex instanceof CompletionException) {
					CompletionException exCompl = (CompletionException) ex;
					if (exCompl.getCause() != null)
						ex = exCompl.getCause();
				}

				if (sender != null)
					Lang.ERROR_OCCURED.send(sender, friendlyErrorMessage);
				severe(friendlyErrorMessage, ex);
			}
		};
	}

	public <T> BiConsumer<T, Throwable> logError(String friendlyErrorMessage, CommandSender sender) {
		return logError(null, friendlyErrorMessage, sender);
	}

	public <T> BiConsumer<T, Throwable> logError(String friendlyErrorMessage) {
		return logError(null, friendlyErrorMessage, null);
	}

	public <T> BiConsumer<T, Throwable> logError() {
		return logError(null, null, null);
	}

}
