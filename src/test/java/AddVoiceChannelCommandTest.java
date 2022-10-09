import bot.commands.AddVoiceChannelCommand;

import net.dv8tion.jda.api.entities.Guild;

import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.channels.Channel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

//TODO: Tests fertig
@ExtendWith(MockitoExtension.class)
public class AddVoiceChannelCommandTest {

    @Mock
    MessageReceivedEvent event;

    String categoryName = "test_category";

    @InjectMocks
    HashMap<String, LocalDateTime> voicechannelCreationTime;

    AddVoiceChannelCommand underTest = new AddVoiceChannelCommand(categoryName);

    @Captor
    ArgumentCaptor<java.util.function.Consumer> captor;

    @Test
    public void canNotBeUsedOutsideOfGuild(){
        //given
        when(event.isFromGuild()).thenReturn(false);

        //when
        String message = underTest.run("test", event);

        //then
        assertEquals("Dieser Befehl kann leider nur auf Servern genutzt werden",  message);

    }

    @Test
    void needsNonEmptyArgs(){
        //given
        when(event.isFromGuild()).thenReturn(true);

        //when
        String message = underTest.run("", event);

        //then
        assertEquals(underTest.getLongDesc(), message);
    }
    // TODO: test fertigstellen
    @Test
    void createsVoiceChannel(){
        //given
        Guild guild = mock(Guild.class);
        Category category = mock(Category.class);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        when(event.isFromGuild()).thenReturn(true);
        when(event.getGuild()).thenReturn(guild);
        when(guild.getCategoriesByName(categoryName, true)).thenReturn(categories);

        String args = "channel_name";
        ChannelAction<VoiceChannel> channelAction = mock(ChannelAction.class);
        when(guild.createVoiceChannel("[TEMP] " + args, category)).thenReturn(channelAction);
        VoiceChannel channel = mock(VoiceChannel.class);

        //when
        String message = underTest.run(args, event);

        //then
        verify(guild, times(1)).createVoiceChannel("[TEMP] " + args, category);
        verify(channelAction, times(1)).queue(captor.capture());
        assertEquals("Kanal \"" + args + "\" erstellt! Bitte gehe schnell in den Kanal, bevor er gelöscht wird", message);
    }

    @Test
    void noCategoryFound(){
        Guild guild = mock(Guild.class);
        List<Category> categories = new ArrayList<>();
        when(event.isFromGuild()).thenReturn(true);
        when(event.getGuild()).thenReturn(guild);
        when(guild.getCategoriesByName(categoryName, true)).thenReturn(categories);
        String args = "channel_name";

        //when
        String message = underTest.run(args, event);

        //then
        assertEquals("There is no category with the configured name " + categoryName + ". Please contact an administrator.", message);
    }
}
