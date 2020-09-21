package de.gleyder.admiral.core.error;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.TestOnly;

import java.util.Collection;
import java.util.stream.Collectors;

@ToString
@RequiredArgsConstructor
public class MultipleCommandError implements CommandError {

  private final Collection<CommandError> errorCollection;

  @Override
  public String getSimple() {
    return errorCollection.stream()
        .map(CommandError::getSimple)
        .collect(Collectors.joining("\n"));
  }

  @Override
  public String getDetailed() {
    return errorCollection.stream()
        .map(CommandError::getDetailed)
        .collect(Collectors.joining("\n"));
  }

  @TestOnly
  public Collection<CommandError> getErrorCollection() {
    return errorCollection;
  }
}
