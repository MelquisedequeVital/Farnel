package br.edu.ifpb.gps.farnel.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.Getter;

/**
 * Filtros do catálogo. Cada filtro exclui os pratos cujo campo
 * {@code alergenos} contenha algum dos termos informados.
 */
@Getter
public enum FiltroCardapio {

	SEM_GLUTEN("sem-gluten", "Sem Glúten", "Cereais ancestrais, mandiocaria e sementes de alta pureza.",
			List.of("gluten", "trigo", "centeio", "cevada", "aveia"),
			"https://lh3.googleusercontent.com/aida-public/AB6AXuBdl_aFl-lZijK7_jEbgul1KivqnuL1MjQzGciYiCuZRwGhmgalMW8DF38csX4eYVDArwLArweraw9X1Spy4_a96FWZ9jpbM6wotEmWRjI4vx0xdL_pdNzv01ro6QmjVf_xgyeDeQagpANwkz2DOOB_62MU9r0NyYy9v2OKLu2nF82Q5Pw2dzPdLGuXDPlJ3QytIpi8UJm4M7Ydml6nnxM4Fl6Gh3jlR77444_j0WEKbhxljn5P21QQ"),
	SEM_LACTOSE("sem-lactose", "Sem Lactose", "Emulsões de castanhas, leite de coco artesanal e azeites crus.",
			List.of("lactose", "leite"),
			"https://lh3.googleusercontent.com/aida-public/AB6AXuA29W1T5MiyeFhzzYHO_o0KEelf-MD-kkKP4XwhRzj5fob6chjEN1xaj_3v0QHWV95MN9dUT_NDAJQ6Krs_-lqN4K3Y38BNYrIjqRFiZ2GFqP2DBHrvd2ASLi7_xWIdXWE4UV5tFcEH1qBL3XhqNSjmHDChPVs5nLYnk19dU0v2aVt5Tt8eX-VJzLgM7xLniY0KkPrau-205vsHhD1OJvpJeu5Rr99JaHt7M8gRjY__9ZnwbsYxlf1s"),
	SEM_OVO("sem-ovo", "Sem Ovo", "Massas e preparos sem ovos ou derivados.",
			List.of("ovo"),
			"https://lh3.googleusercontent.com/aida-public/AB6AXuAe-Tkpq8lk2HHxa5QECJB6GWQh8hT-b9QT6OIesmignB2Ml6gB7Z-u-QbAAUhGi0EcEUHfqGWL9xMbSByT0ukpLXSA-M1cRJlmfWdWPLVD6g0_GCi0bU37o_cD5fHRXg2G8Y6Q1RYX-oFKlUZQcLIPjdvdJYQKxbqDQhqgvCsDdKM5Rs912TbTwvRr22hePUsbVZwrjVonbILycw5uUmHGuIfD1UYrbmQJ-4KtqENbYhwoz5hUeLYV"),
	SEM_SOJA("sem-soja", "Sem Soja", "Receitas livres de soja e seus derivados.",
			List.of("soja"),
			"https://lh3.googleusercontent.com/aida-public/AB6AXuDtNJSXBupbdoTXjyRMYRMJgkCxkicKdRxTvuAblyMZXMiMXr08Qwqd_yTzOJOYpl8mMfAHUAQexx9Aerz-xv7pytelsbaDd3hEw21qkdCwb_DPHTHRxIqTk-91OtK4ARwd7zNueejYsiwjzgwTbJO9Bh0XoR9FCkeoTYV6ZJ_bD1xv7C-fFBwlzusUkZF_MbfQVFAwRbuAswgltrvVoSV-nZ4a48GpaUFH1m-JPRcJlkx9wHlKZndC"),
	SEM_AMENDOIM("sem-amendoim", "Sem Amendoim", "Preparos sem amendoim e oleaginosas afins.",
			List.of("amendoim"),
			"https://lh3.googleusercontent.com/aida-public/AB6AXuB-Gwd9MfkADSIW5pfyig_ZRIhZOHnFgnZiliLOPH3uAr5OjnDPD7t5sckJuIp2RZqHh0mJ8MQff8Mpcnykb6laQpJyMb3rHXEKF2aC8UzP31hl17A__MmHD2UKNByzwmkHXxvDCFQ0U21PvWzELuGfovEUROKJrL2xaKnZyTp0obsL2Hozd8CZdLJyXCE8EQh8TBS7Z3sbxGh6h1tML5_V6m2aE0UQT3jf-P_UjV4WBMlRk4IJm3so");

	private final String slug;
	private final String rotulo;
	private final String descricao;
	private final List<String> termos;
	/** Imagem ilustrativa usada na faixa de categorias do catálogo. */
	private final String imagem;

	FiltroCardapio(String slug, String rotulo, String descricao, List<String> termos, String imagem) {
		this.slug = slug;
		this.rotulo = rotulo;
		this.descricao = descricao;
		this.termos = termos;
		this.imagem = imagem;
	}

	public static Optional<FiltroCardapio> doSlug(String slug) {
		return Arrays.stream(values())
				.filter(f -> f.slug.equalsIgnoreCase(slug))
				.findFirst();
	}

}
