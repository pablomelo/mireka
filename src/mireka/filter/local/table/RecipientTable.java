package mireka.filter.local.table;

import java.util.ArrayList;
import java.util.List;

import mireka.address.Recipient;
import mireka.destination.Destination;

/**
 * RecipientTable contains a list of other {@link RecipientDestinationMapper}
 * instances, and search each element of the list to lookup the destination
 * assigned to a recipient.
 */
public class RecipientTable implements RecipientDestinationMapper {
    private final List<RecipientDestinationMapper> mappers =
            new ArrayList<RecipientDestinationMapper>();

    @Override
    public Destination lookup(Recipient recipient) {
        for (RecipientDestinationMapper mapper : mappers) {
            Destination destination = mapper.lookup(recipient);
            if (destination != null)
                return destination;
        }
        return null;
    }

    public void addMapper(RecipientDestinationMapper mapper) {
        mappers.add(mapper);
    }

}
