package rip.thecraft.brawl.challenge;

import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class PlayerChallenge {

    private final String id;
    @NonNull private long expiresAt;

    private Map<String, Object> data = new HashMap<>();

}
