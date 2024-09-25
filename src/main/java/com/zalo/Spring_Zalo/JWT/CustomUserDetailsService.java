	package com.zalo.Spring_Zalo.JWT;

	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.security.core.GrantedAuthority;
	import org.springframework.security.core.authority.AuthorityUtils;
	import org.springframework.security.core.authority.SimpleGrantedAuthority;
	import org.springframework.security.core.userdetails.UserDetails;
	import org.springframework.security.core.userdetails.UserDetailsService;
	import org.springframework.security.core.userdetails.UsernameNotFoundException;
	import org.springframework.stereotype.Service;

	import com.zalo.Spring_Zalo.Entities.Roles;
	import com.zalo.Spring_Zalo.Entities.User;
	import com.zalo.Spring_Zalo.Repo.UserRepo;




	import java.util.ArrayList;
	import java.util.Collection;
	import java.util.Collections;
	import java.util.List;

	@Service
	public class CustomUserDetailsService implements UserDetailsService {

		@Autowired
		private UserRepo userRepo;

		private final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

		/**
		 *
		 * @param username
		 * @return
		 * @throws UsernameNotFoundException
		 */
		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			// Lấy thông tin người dùng từ cơ sở dữ liệu
			
			User user = userRepo.findByUserName(username);

			// Log thông tin về người dùng
			logger.info("Loaded user: " + user.getUsername());

			// Ánh xạ các vai trò của người dùng thành các quyền (authorities)
			Collection<GrantedAuthority> authorities = mapRolesToAuthorities(user.getRole());

			// Log các quyền của người dùng
			logger.info("Authorities: " + authorities);

			// Trả về một đối tượng UserDetails
			return new org.springframework.security.core.userdetails.User(
					user.getUsername(),
					user.getPassword(),
					authorities);
		}

		public Collection<GrantedAuthority> mapRolesToAuthorities(Roles roles) {
			List<GrantedAuthority> authorities = new ArrayList<>();
			if (roles != null) {
				// Lấy tên của vai trò từ đối tượng Roles
				String roleName = roles.getRoleName().name();
				// Tạo quyền dựa trên tên của vai trò
				SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);
				authorities.add(authority);
			}
			return authorities;
		}

	}
