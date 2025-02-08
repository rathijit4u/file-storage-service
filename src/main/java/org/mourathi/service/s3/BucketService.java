package org.mourathi.service.s3;

import io.minio.messages.Bucket;
import org.mourathi.auth.UserPrincipal;
import org.mourathi.dto.BucketDto;
import org.mourathi.model.User;
import org.mourathi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BucketService implements IBucketService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MinIOService minIOService;

    @Override
    public BucketDto createBucket(String name) throws Exception {
        minIOService.makeBucket(name);

        return this.getBucket(name);
    }

    @Override
    public void deleteBucket(String name){
        minIOService.removeBucket(name);
    }

    @Override
    public List<BucketDto> getAllBuckets(){
        return minIOService.listBuckets().stream()
                .filter(Objects::nonNull)
                .map(bucket -> new BucketDto(bucket.name(), bucket.creationDate().toLocalDate()))
                .collect(Collectors.toList());
    }

    @Override
    public BucketDto getBucket(String name) throws Exception {
        Bucket bucket = minIOService.getBucket(name);
        if (bucket != null)
            return new BucketDto(bucket.name(), bucket.creationDate().toLocalDate());
        else
            throw new Exception("Bucket - '" + name + "' not found");
    }

    @Service
    public static class FSUserDetailsService implements UserDetailsService {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        private UserRepository userRepository;
        /**
         * Locates the user based on the username. In the actual implementation, the search
         * may possibly be case sensitive, or case insensitive depending on how the
         * implementation instance is configured. In this case, the <code>UserDetails</code>
         * object that comes back may have a username that is of a different case than what
         * was actually requested..
         *
         * @param username the username identifying the user whose data is required.
         * @return a fully populated user record (never <code>null</code>)
         * @throws UsernameNotFoundException if the user could not be found or the user has no
         *                                   GrantedAuthority
         */
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUserName(username);
            if(user == null){
                this.logger.error("No user found - {}", username);
                throw new UsernameNotFoundException("No user found - " + username);
            }

            return new UserPrincipal(user);
        }
    }
}
