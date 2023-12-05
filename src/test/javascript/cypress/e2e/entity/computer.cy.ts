import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Computer e2e test', () => {
  const computerPageUrl = '/computer';
  const computerPageUrlPattern = new RegExp('/computer(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const computerSample = { name: 'notwithstanding geez' };

  let computer;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/computers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/computers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/computers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (computer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/computers/${computer.id}`,
      }).then(() => {
        computer = undefined;
      });
    }
  });

  it('Computers menu should load Computers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('computer');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Computer').should('exist');
    cy.url().should('match', computerPageUrlPattern);
  });

  describe('Computer page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(computerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Computer page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/computer/new$'));
        cy.getEntityCreateUpdateHeading('Computer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', computerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/computers',
          body: computerSample,
        }).then(({ body }) => {
          computer = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/computers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [computer],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(computerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Computer page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('computer');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', computerPageUrlPattern);
      });

      it('edit button click should load edit Computer page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Computer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', computerPageUrlPattern);
      });

      it('edit button click should load edit Computer page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Computer');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', computerPageUrlPattern);
      });

      it('last delete button click should delete instance of Computer', () => {
        cy.intercept('GET', '/api/computers/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('computer').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', computerPageUrlPattern);

        computer = undefined;
      });
    });
  });

  describe('new Computer page', () => {
    beforeEach(() => {
      cy.visit(`${computerPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Computer');
    });

    it('should create an instance of Computer', () => {
      cy.get(`[data-cy="name"]`).type('ah sanctity hence');
      cy.get(`[data-cy="name"]`).should('have.value', 'ah sanctity hence');

      cy.get(`[data-cy="introduced"]`).type('2023-12-05');
      cy.get(`[data-cy="introduced"]`).blur();
      cy.get(`[data-cy="introduced"]`).should('have.value', '2023-12-05');

      cy.get(`[data-cy="removed"]`).type('2023-12-05');
      cy.get(`[data-cy="removed"]`).blur();
      cy.get(`[data-cy="removed"]`).should('have.value', '2023-12-05');

      cy.get(`[data-cy="hardware"]`).type('28');
      cy.get(`[data-cy="hardware"]`).should('have.value', '28');

      cy.get(`[data-cy="software"]`).type('23');
      cy.get(`[data-cy="software"]`).should('have.value', '23');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        computer = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', computerPageUrlPattern);
    });
  });
});
