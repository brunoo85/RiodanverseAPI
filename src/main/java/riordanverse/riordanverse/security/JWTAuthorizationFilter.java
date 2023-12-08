package riordanverse.riordanverse.security;

import static riordanverse.riordanverse.security.SecurityConstants.HEADER_STRING;
import static riordanverse.riordanverse.security.SecurityConstants.SECRET;
import static riordanverse.riordanverse.security.SecurityConstants.TOKEN_PREFIX;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import riordanverse.riordanverse.entities.Usuario;
import riordanverse.riordanverse.services.UsuarioService;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	UsuarioService usuarioService;

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager, UsuarioService usuarioService) {
		super(authenticationManager);
		this.usuarioService = usuarioService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req,
									HttpServletResponse res,
									FilterChain chain) throws IOException, ServletException {
		try {
			String header = req.getHeader(HEADER_STRING);

			if (header == null || !header.startsWith(TOKEN_PREFIX)) {
				chain.doFilter(req, res);
				return;
			}

			UsernamePasswordAuthenticationToken authentication = getAuthentication(req, res);

			SecurityContextHolder.getContext().setAuthentication(authentication);
			chain.doFilter(req, res);
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.setHeader("Content-Type", "application/json; charset=UTF-8");
			res.setCharacterEncoding("UTF-8");
			String json = String.format("{ \"msg\" : \"%s\" }", e.getMessage());
			res.getWriter().write(json);
		}
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String token = request.getHeader(HEADER_STRING);

		if (token != null) {
			DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
					.build()
					.verify(token.replace(TOKEN_PREFIX, ""));

			String login = decodedJWT.getSubject();

			Usuario usuario = usuarioService.getUsuarioByLogin(login);

			String freshToken = TokenUtil.getToken(login);
			response.addHeader("fresh-token", freshToken);

			return new UsernamePasswordAuthenticationToken(
					usuario.getLogin(),
					usuario.getSenha(),
					Arrays.asList(new SimpleGrantedAuthority(usuario.getFuncao().toString())));
		}

		return null;
	}

}