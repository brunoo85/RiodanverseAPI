package riordanverse.riordanverse.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import riordanverse.riordanverse.entities.Livro;
import riordanverse.riordanverse.services.LivroService;

@RestController
@RequestMapping("/livro")
public class LivroController {

	@Autowired
	LivroService livroService;

	@GetMapping("/id/{idLivro}")
	public Livro getLivroById(@PathVariable Integer idLivro) {
		Livro livro = livroService.getLivroById(idLivro);

		if (livro == null) {
			throw new NoSuchElementException("Não foram encontrados resultados para essa busca!");
		}

		return livro;
	}

	@GetMapping("/nome/{nomeLivro}")
	public Livro getLivroByNome(@PathVariable String nomeLivro) {
		Livro livro = livroService.getLivroByNome(nomeLivro);

		if (livro == null) {
			throw new NoSuchElementException("Não foram encontrados resultados para essa busca!");
		}

		return livro;
	}

	@GetMapping("/lancamento/{lancamentoLivro}")
	public List<Livro> getLivroByLancamento(@PathVariable Integer lancamentoLivro) {
		List<Livro> livros = livroService.getLivrosByLancamento(lancamentoLivro);

		if (livros.isEmpty()) {
			throw new NoSuchElementException("Não foram encontrados resultados para essa busca!");
		}

		return livros;
	}

	@GetMapping("/paginas/{pagsLivro}")
	public List<Livro> getLivroByQuantidadePaginas(@PathVariable Integer pagsLivro) {
		List<Livro> livros = livroService.getLivrosByQuantidadePaginas(pagsLivro);

		if (livros.isEmpty()) {
			throw new NoSuchElementException("Não foram encontrados resultados para essa busca!");
		}

		return livros;
	}

	@GetMapping("/mitologia/{mitologiaLivro}")
	public List<Livro> getLivroByMitologia(@PathVariable String mitologiaLivro) {
		List<Livro> livros = livroService.getLivrosByMitologia(mitologiaLivro);

		if (livros.isEmpty()) {
			throw new NoSuchElementException("Não foram encontrados resultados para essa busca!");
		}

		return livros;
	}

	@GetMapping("/all")
	public List<Livro> getAllLivros() {
		List<Livro> livros = livroService.getAllLivros();

		if (livros.isEmpty()) {
			throw new NoSuchElementException("Não foram encontrados resultados para essa busca!");
		}

		return livros;
	}

	@PostMapping
	@Secured(value = {"ROLE_FUNCIONARIO", "ROLE_ADMIN"})
	public String cadastrarLivro(@RequestBody Livro livro) {
		try {
			livroService.salvar(livro);

			String nome = livro.getNome();
			return "Livro " + nome + " cadastrado com sucesso!";
		} catch (RuntimeException e) {
			return "Erro ao cadastrar livro: " + e.getMessage();
		}
	}

	@PutMapping
	@Secured(value = {"ROLE_FUNCIONARIO", "ROLE_ADMIN"})
	public String atualizarLivro(@RequestBody Livro livro) {
		String nome = livro.getNome();
		Livro existente = livroService.getLivroByNome(nome);

		if (existente != null) {
			throw new IllegalArgumentException("Já existe um livro com o nome: " + nome);
		}

		livroService.atualizar(livro);
		String feedback = "Livro " + nome + " atualizado com sucesso!";
		return feedback;
	}

	@DeleteMapping("/id/{idLivro}")
	@Secured(value = {"ROLE_ADMIN"})
	public String removerLivro(@PathVariable Integer idLivro) {
		Livro existente = livroService.getLivroById(idLivro);

		livroService.remover(idLivro);
		String feedback = "Livro " + existente.getNome() + " removido com sucesso!";
		return feedback;
	}

}
